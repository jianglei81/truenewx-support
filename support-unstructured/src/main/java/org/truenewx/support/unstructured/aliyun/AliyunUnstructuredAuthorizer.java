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
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * 阿里云的非结构化存储授权器
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAuthorizer implements UnstructuredAuthorizer {

    private int tempReadExpiredSeconds = 30; // 默认30秒钟
    private AliyunAccount account;
    private AliyunPolicyBuilder policyBuilder;
    private AliyunStsRoleAssumer readStsRoleAssumer;
    private AliyunStsRoleAssumer writeStsRoleAssumer;

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
     * @param readStsRoleAssumer
     *            读权限的STS临时角色假扮器
     */
    public void setReadStsRoleAssumer(final AliyunStsRoleAssumer readStsRoleAssumer) {
        this.readStsRoleAssumer = readStsRoleAssumer;
    }

    /**
     * @param writeStsRoleAssumer
     *            写权限的STS临时角色假扮器
     */
    public void setWriteStsRoleAssumer(final AliyunStsRoleAssumer writeStsRoleAssumer) {
        this.writeStsRoleAssumer = writeStsRoleAssumer;
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
        final String policyDocument = this.policyBuilder.buildWriteDocument(bucket, path);
        final Credentials credentials = this.writeStsRoleAssumer.assumeRole(userKey,
                policyDocument);
        if (credentials != null) {
            final UnstructuredAccess access = new UnstructuredAccess(credentials.getAccessKeyId(),
                    credentials.getAccessKeySecret());
            access.setTempToken(credentials.getSecurityToken());
            access.setExpiredTime(parseExpiredTime(credentials.getExpiration()));
            return access;
        }
        return null;
    }

    private Date parseExpiredTime(String expiration) {
        expiration = expiration.replace("T", Strings.SPACE).replace("Z", Strings.EMPTY);
        Date expiredTime = DateUtil.parseLong(expiration);
        expiredTime = DateUtil.addHours(expiredTime, 8); // 必须添加8小时时区差
        return expiredTime;
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
                final Credentials credentials = this.readStsRoleAssumer.assumeRole(userKey,
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
