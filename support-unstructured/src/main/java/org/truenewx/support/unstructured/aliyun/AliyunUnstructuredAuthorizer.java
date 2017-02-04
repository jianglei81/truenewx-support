package org.truenewx.support.unstructured.aliyun;

import java.net.URL;
import java.util.Date;

import org.truenewx.core.Strings;
import org.truenewx.core.util.DateUtil;
import org.truenewx.support.unstructured.UnstructuredAuthorizer;
import org.truenewx.support.unstructured.model.UnstructuredAccess;
import org.truenewx.support.unstructured.model.UnstructuredProvider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectAcl;
import com.aliyun.oss.model.ObjectPermission;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToUserRequest;
import com.aliyuncs.ram.model.v20150501.CreatePolicyRequest;
import com.aliyuncs.ram.model.v20150501.GetPolicyRequest;
import com.aliyuncs.ram.model.v20150501.GetPolicyResponse;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * 阿里云的非结构化存储授权器
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAuthorizer implements UnstructuredAuthorizer {

    private int tempReadExpiredSeconds = 30;
    private AliyunAccount account;
    private AliyunPolicyBuilder policyBuilder;
    private AliyunStsRoleAssumer stsRoleAssumer;
    private AliyunUnstructuredAccessProvider accessProvider;

    /**
     * @param tempReadExpiredSeconds
     *            临时读取权限过期秒数
     */
    public void setTempReadExpiredSeconds(final int tempReadExpiredSeconds) {
        this.tempReadExpiredSeconds = tempReadExpiredSeconds;
    }

    /**
     * @param account
     *            阿里云账户信息
     */
    public void setAccount(final AliyunAccount account) {
        this.account = account;
    }

    /**
     * @param policyBuilder
     *            授权方针构建器
     */
    public void setPolicyBuilder(final AliyunPolicyBuilder policyBuilder) {
        this.policyBuilder = policyBuilder;
    }

    /**
     * @param stsRoleAssumer
     *            STS临时角色假扮器
     */
    public void setStsRoleAssumer(final AliyunStsRoleAssumer stsRoleAssumer) {
        this.stsRoleAssumer = stsRoleAssumer;
    }

    /**
     * @param accessProvider
     *            访问参数提供者
     */
    public void setAccessProvider(final AliyunUnstructuredAccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.ALIYUN;
    }

    @Override
    public String getRegion() {
        return this.account.getOssRegion();
    }

    @Override
    public String getHost() {
        return this.account.getOssEndpoint();
    }

    @Override
    public String standardizePath(final String path) {
        if (path.startsWith(Strings.SLASH)) { // 不能以斜杠开头
            return path.substring(1);
        }
        return path;
    }

    @Override
    public UnstructuredAccess authorizePrivateWrite(final String userKey, final String bucket,
            final String path) {
        final UnstructuredAccess access = this.accessProvider.getUnstructuredAccess(userKey);
        if (access != null) { // 出现底层错误时会为null
            final String policyName = ensureWritePolicy(bucket, path);
            if (policyName != null) {
                attachPolicy(policyName, userKey);
                return access;
            }
        }
        return null;
    }

    private String ensureWritePolicy(final String bucket, final String path) {
        final String policyName = this.policyBuilder.buildName("Write-", bucket, path);

        final GetPolicyRequest getPolicyRequest = new GetPolicyRequest();
        getPolicyRequest.setPolicyType("Custom");
        getPolicyRequest.setPolicyName(policyName);
        try {
            final GetPolicyResponse getPolicyResponse = this.account.getAcsClient()
                    .getAcsResponse(getPolicyRequest);
            if (getPolicyResponse.getPolicy() != null) {
                return policyName; // 已存在该授权方针，则直接返回
            }
        } catch (final ClientException e) {
            if (!"EntityNotExist.Policy".equals(e.getErrCode())) {
                e.printStackTrace();
            }
        }

        // 创建授权方针
        final CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest();
        createPolicyRequest.setPolicyName(policyName);
        final String policyDocument = this.policyBuilder.buildWriteDocument(bucket, path);
        createPolicyRequest.setPolicyDocument(policyDocument);
        createPolicyRequest
                .setDescription("Write for " + bucket + Strings.COLON + path + Strings.ASTERISK);
        try {
            this.account.getAcsClient().doAction(createPolicyRequest);
            return policyName;
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void attachPolicy(final String policyName, final String userKey) {
        final AttachPolicyToUserRequest request = new AttachPolicyToUserRequest();
        request.setPolicyType("Custom");
        request.setPolicyName(policyName);
        request.setUserName(userKey);
        try {
            this.account.getAcsClient().doAction(request);
        } catch (final ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authorizePublicRead(final String bucket, final String path) {
        this.account.getOssClient().setObjectAcl(bucket, path, CannedAccessControlList.PublicRead);
    }

    private boolean isPublicRead(final String bucket, final String path) {
        final ObjectAcl acl = this.account.getOssClient().getObjectAcl(bucket, path);
        final ObjectPermission permission = acl.getPermission();
        return permission == ObjectPermission.PublicRead
                || permission == ObjectPermission.PublicReadWrite;
    }

    @Override
    public String getReadHttpUrl(final String userKey, final String bucket, String path) {
        final int index = path.indexOf("?");
        String paramString = Strings.EMPTY;
        if (index > 0) {
            paramString = path.substring(index);
            path = path.substring(0, index);
        }
        try {
            if (isPublicRead(bucket, path)) {
                final StringBuffer url = new StringBuffer("http://").append(bucket)
                        .append(Strings.DOT).append(this.account.getOssEndpoint())
                        .append(Strings.SLASH).append(path);
                return url.toString() + paramString;
            } else { // 非公开可读的，授予临时读取权限
                final String policyDocument = this.policyBuilder.buildReadDocument(bucket, path);
                final Credentials credentials = this.stsRoleAssumer.assumeRole(userKey,
                        policyDocument);
                if (credentials != null) {
                    final OSS oss = new OSSClient(this.account.getOssEndpoint(),
                            credentials.getAccessKeyId(), credentials.getAccessKeySecret(),
                            credentials.getSecurityToken());
                    final Date expiration = DateUtil.addSeconds(new Date(),
                            this.tempReadExpiredSeconds);
                    final URL url = oss.generatePresignedUrl(bucket, path, expiration);
                    return url.toString() + paramString;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
