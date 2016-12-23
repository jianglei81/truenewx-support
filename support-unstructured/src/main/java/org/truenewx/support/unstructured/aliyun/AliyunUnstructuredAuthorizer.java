package org.truenewx.support.unstructured.aliyun;

import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
    private String ossEndpoint;
    private String ramRegion = "cn-hangzhou";
    private String adminAccessKeyId;
    private String adminAccessKeySecret;
    private int tempReadExpiredSeconds = 10;
    private IAcsClient acsClient;
    private AliyunPolicyBuilder policyBuilder;
    private AliyunStsRoleAssumer stsRoleAssumer;
    private Map<String, String> accessKeys = new Hashtable<>();

    /**
     * @param ossEndpoint
     *            OSS服务端点
     */
    public void setOssEndpoint(final String ossEndpoint) {
        this.ossEndpoint = ossEndpoint;
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
        UnstructuredAccess access = findAccess(userKey);
        if (access == null) {
            access = createAccess(userKey);
        }
        return access;
    }

    private UnstructuredAccess findAccess(final String userKey) {
        final ListAccessKeysRequest request = new ListAccessKeysRequest();
        request.setUserName(userKey);
        try {
            final ListAccessKeysResponse response = getAcsClient().getAcsResponse(request);
            final List<ListAccessKeysResponse.AccessKey> accessKeys = response.getAccessKeys();
            String id = null;
            String secret = null;
            for (final ListAccessKeysResponse.AccessKey accessKey : accessKeys) {
                final String accessKeyId = accessKey.getAccessKeyId();
                if ("Active".equals(accessKey.getStatus())) {
                    final String accessKeySecret = this.accessKeys.get(accessKeyId);
                    if (accessKeySecret != null) {
                        id = accessKeyId;
                        secret = accessKeySecret;
                        break;
                    } else { // 删除本地未缓存的密钥，因为该密钥已经无法动态获取并使用
                        deleteAccessKey(userKey, accessKeyId);
                    }
                } else { // 禁用的也删除
                    deleteAccessKey(userKey, accessKeyId);
                }
            }
            if (secret == null) { // 未找到有缓存的密钥，则创建新的密钥
                final AccessKey accessKey = createAccessKey(userKey);
                id = accessKey.getAccessKeyId();
                secret = accessKey.getAccessKeySecret();
            }
            final UnstructuredAccess account = new UnstructuredAccess();
            account.setAccessId(id);
            account.setAccessSecret(secret);
            return account;
        } catch (final ClientException e) {
            if (!"EntityNotExist.User".equals(e.getErrCode())) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void deleteAccessKey(final String userKey, final String accessKeyId) {
        final DeleteAccessKeyRequest request = new DeleteAccessKeyRequest();
        request.setUserName(userKey);
        request.setUserAccessKeyId(accessKeyId);
        try {
            getAcsClient().doAction(request);
        } catch (final ClientException e) { // 删除失败并不影响整体逻辑
            e.printStackTrace();
        }
    }

    private CreateAccessKeyResponse.AccessKey createAccessKey(final String userKey)
            throws ClientException {
        // 创建访问密钥
        final CreateAccessKeyRequest createAccessKeyRequest = new CreateAccessKeyRequest();
        createAccessKeyRequest.setUserName(userKey);
        final CreateAccessKeyResponse response = getAcsClient()
                .getAcsResponse(createAccessKeyRequest);
        final AccessKey accessKey = response.getAccessKey();
        this.accessKeys.put(accessKey.getAccessKeyId(), accessKey.getAccessKeySecret());
        return accessKey;
    }

    private UnstructuredAccess createAccess(final String userKey) {
        try {
            // 创建用户
            final CreateUserRequest createUserRequest = new CreateUserRequest();
            createUserRequest.setUserName(userKey);
            getAcsClient().doAction(createUserRequest);

            final CreateAccessKeyResponse.AccessKey accessKey = createAccessKey(userKey);
            final UnstructuredAccess access = new UnstructuredAccess();
            access.setAccessId(accessKey.getAccessKeyId());
            access.setAccessSecret(accessKey.getAccessKeySecret());

            return access;
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return null;
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

}
