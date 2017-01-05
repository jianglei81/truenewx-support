package org.truenewx.support.unstructured.aliyun;

import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToUserRequest;
import com.aliyuncs.ram.model.v20150501.CreateAccessKeyRequest;
import com.aliyuncs.ram.model.v20150501.CreateAccessKeyResponse;
import com.aliyuncs.ram.model.v20150501.CreateAccessKeyResponse.AccessKey;
import com.aliyuncs.ram.model.v20150501.CreatePolicyRequest;
import com.aliyuncs.ram.model.v20150501.CreateUserRequest;
import com.aliyuncs.ram.model.v20150501.DeleteAccessKeyRequest;
import com.aliyuncs.ram.model.v20150501.GetPolicyRequest;
import com.aliyuncs.ram.model.v20150501.GetPolicyResponse;
import com.aliyuncs.ram.model.v20150501.ListAccessKeysRequest;
import com.aliyuncs.ram.model.v20150501.ListAccessKeysResponse;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * 阿里云的非结构化存储授权器
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAuthorizer implements UnstructuredAuthorizer {

    private OSS oss;
    private String ossRegion;
    private String ossEndpoint;
    private String ramRegion = "cn-hangzhou";
    private String adminAccessKeyId;
    private String adminAccessKeySecret;
    private int tempReadExpiredSeconds = 10;
    private IAcsClient acsClient;
    private AliyunPolicyBuilder policyBuilder;
    private AliyunStsRoleAssumer stsRoleAssumer;
    private Map<String, UnstructuredAccess> accesses = new Hashtable<>();

    /**
     *
     * @author liaozhan
     *
     * @param ossRegion
     *            OSS地区
     */
    public void setOssRegion(final String ossRegion) {
        this.ossRegion = ossRegion;
        if (StringUtils.isNotBlank(this.ossRegion)) {
            this.ossEndpoint = this.ossRegion + ".aliyuncs.com";
        } else {
            this.ossEndpoint = null;
        }
    }

    /**
     * @param ramRegion
     *            RAM服务所在区域
     */
    public void setRamRegion(final String ramRegion) {
        this.ramRegion = ramRegion;
    }

    /**
     * @param adminAccessKeyId
     *            管理账号访问id
     */
    public void setAdminAccessKeyId(final String adminAccessKeyId) {
        this.adminAccessKeyId = adminAccessKeyId;
    }

    /**
     * @param adminAccessKeySecret
     *            管理员账号访问私钥
     */
    public void setAdminAccessKeySecret(final String adminAccessKeySecret) {
        this.adminAccessKeySecret = adminAccessKeySecret;
    }

    /**
     * @param tempReadExpiredSeconds
     *            临时读取权限过期秒数
     */
    public void setTempReadExpiredSeconds(final int tempReadExpiredSeconds) {
        this.tempReadExpiredSeconds = tempReadExpiredSeconds;
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

    @Override
    protected void finalize() throws Throwable {
        if (this.oss != null) {
            this.oss.shutdown();
        }
    }

    private OSS getOss() {
        if (this.oss == null) {
            this.oss = new OSSClient(this.ossEndpoint, this.adminAccessKeyId,
                    this.adminAccessKeySecret);
        }
        return this.oss;
    }

    private IAcsClient getAcsClient() {
        if (this.acsClient == null) {
            final IClientProfile profile = DefaultProfile.getProfile(this.ramRegion,
                    this.adminAccessKeyId, this.adminAccessKeySecret);
            this.acsClient = new DefaultAcsClient(profile);
        }
        return this.acsClient;
    }

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.ALIYUN;
    }

    @Override
    public String getHost() {
        return this.ossEndpoint;
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
        final UnstructuredAccess access = ensureAccess(userKey);
        final String policyName = ensureWritePolicy(bucket, path);
        if (policyName != null) {
            attachPolicy(policyName, userKey);
            return access;
        }
        return null;
    }

    private UnstructuredAccess ensureAccess(final String userKey) {
        final UnstructuredAccess access = this.accesses.get(userKey);
        if (access != null) {
            return access;
        }
        // 未缓存该用户的密钥，则创建新的密钥
        try {
            // 先删除该用户的所有已有密钥，避免数量超过阿里云限制
            deleteAllAccessKeys(userKey);
        } catch (final ClientException e) {
            if ("EntityNotExist.User".equals(e.getErrCode())) { // 用户不存在，则创建用户
                if (!createUser(userKey)) { // 用户创建失败，则直接返回null
                    return null;
                }
            } else { // 不是用户不存在的错误，则返回null
                e.printStackTrace();
                return null;
            }
        }
        // 此处可确保用户存在但没有密钥，此时创建密钥
        return createAccess(userKey);
    }

    private UnstructuredAccess createAccess(final String userKey) {
        final CreateAccessKeyRequest request = new CreateAccessKeyRequest();
        request.setUserName(userKey);
        try {
            final CreateAccessKeyResponse response = getAcsClient().getAcsResponse(request);
            final AccessKey accessKey = response.getAccessKey();
            final UnstructuredAccess access = new UnstructuredAccess();
            access.setAccessId(accessKey.getAccessKeyId());
            access.setAccessSecret(accessKey.getAccessKeySecret());
            // 缓存密钥
            this.accesses.put(userKey, access);
            return access;
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteAllAccessKeys(final String userKey) throws ServerException, ClientException {
        final ListAccessKeysRequest listAccessKeysRequest = new ListAccessKeysRequest();
        listAccessKeysRequest.setUserName(userKey);
        final ListAccessKeysResponse response = getAcsClient()
                .getAcsResponse(listAccessKeysRequest);
        for (final ListAccessKeysResponse.AccessKey accessKey : response.getAccessKeys()) {
            final DeleteAccessKeyRequest request = new DeleteAccessKeyRequest();
            request.setUserName(userKey);
            request.setUserAccessKeyId(accessKey.getAccessKeyId());
            try {
                getAcsClient().doAction(request);
            } catch (final ClientException e) { // 删除失败并不影响整体逻辑
                e.printStackTrace();
            }
        }
    }

    private boolean createUser(final String userKey) {
        final CreateUserRequest request = new CreateUserRequest();
        request.setUserName(userKey);
        try {
            getAcsClient().doAction(request);
            return true;
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String ensureWritePolicy(final String bucket, final String path) {
        final String policyName = this.policyBuilder.buildName("Write-", bucket, path);

        final GetPolicyRequest getPolicyRequest = new GetPolicyRequest();
        getPolicyRequest.setPolicyType("Custom");
        getPolicyRequest.setPolicyName(policyName);
        try {
            final GetPolicyResponse getPolicyResponse = getAcsClient()
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
            getAcsClient().doAction(createPolicyRequest);
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
            getAcsClient().doAction(request);
        } catch (final ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authorizePublicRead(final String bucket, final String path) {
        getOss().setObjectAcl(bucket, path, CannedAccessControlList.PublicRead);
    }

    private boolean isPublicRead(final String bucket, final String path) {
        final ObjectAcl acl = getOss().getObjectAcl(bucket, path);
        final ObjectPermission permission = acl.getPermission();
        return permission == ObjectPermission.PublicRead
                || permission == ObjectPermission.PublicReadWrite;
    }

    @Override
    public String getReadHttpUrl(final String userKey, final String bucket, final String path) {
        if (isPublicRead(bucket, path)) {
            final StringBuffer url = new StringBuffer("http://").append(bucket).append(Strings.DOT)
                    .append(this.ossEndpoint).append(Strings.SLASH).append(path);
            return url.toString();
        } else {
            final String policyDocument = this.policyBuilder.buildReadDocument(bucket, path);
            final Credentials credentials = this.stsRoleAssumer.assumeRole(getAcsClient(), userKey,
                    policyDocument);
            if (credentials != null) {
                final OSS oss = new OSSClient(this.ossEndpoint, credentials.getAccessKeyId(),
                        credentials.getAccessKeySecret(), credentials.getSecurityToken());
                final Date expiration = DateUtil.addSeconds(new Date(),
                        this.tempReadExpiredSeconds);
                final URL url = oss.generatePresignedUrl(bucket, path, expiration);
                return url.toString();
            }
            return null;
        }

    }

    @Override
    public String getRegion() {
        return this.ossRegion;
    }

}
