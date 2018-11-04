package org.truenewx.support.unstructured.core.aliyun;

import java.net.URL;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.core.util.DateUtil;
import org.truenewx.support.unstructured.core.UnstructuredAuthorizer;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

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

    public AliyunUnstructuredAuthorizer(AliyunAccount account) {
        this.account = account;
        this.policyBuilder = new AliyunPolicyBuilder(account);
    }

    /**
     * @param tempReadExpiredSeconds
     *            临时读取权限过期秒数
     */
    public void setTempReadExpiredSeconds(int tempReadExpiredSeconds) {
        this.tempReadExpiredSeconds = tempReadExpiredSeconds;
    }

    /**
     * @param readStsRoleName
     *            读权限的STS临时角色名
     */
    public void setReadStsRoleName(String readStsRoleName) {
        this.readStsRoleAssumer = new AliyunStsRoleAssumer(this.account, readStsRoleName);
    }

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.ALIYUN;
    }

    @Override
    public void authorizePublicRead(String bucket, String path) {
        // TODO 避免同样的路径反复多次申请公开读
        this.account.getOssClient().setObjectAcl(bucket, path, CannedAccessControlList.PublicRead);
    }

    private boolean isPublicRead(String bucket, String path) {
        ObjectAcl acl = this.account.getOssClient().getObjectAcl(bucket, path);
        ObjectPermission permission = acl.getPermission();
        return permission == ObjectPermission.PublicRead
                || permission == ObjectPermission.PublicReadWrite;
    }

    protected String getReadHost(String bucket) {
        return bucket + Strings.DOT + this.account.getOssEndpoint();
    }

    @Override
    public String getReadUrl(String userKey, String bucket, String path) {
        int index = path.indexOf("?");
        String paramString = Strings.EMPTY;
        if (index > 0) {
            paramString = path.substring(index);
            path = path.substring(0, index);
        }
        try {
            if (isPublicRead(bucket, path)) {
                // 以双斜杠开头，表示采用当前上下文的相同协议
                StringBuffer url = new StringBuffer("//").append(getReadHost(bucket))
                        .append(Strings.SLASH).append(path);
                return url.toString() + paramString;
            } else if (this.readStsRoleAssumer != null) { // 非公开可读的，授予临时读取权限
                String policyDocument = this.policyBuilder.buildReadDocument(bucket, path);
                Credentials credentials = this.readStsRoleAssumer.assumeRole(userKey,
                        policyDocument);
                if (credentials != null) {
                    OSS oss = new OSSClient(this.account.getOssEndpoint(),
                            credentials.getAccessKeyId(), credentials.getAccessKeySecret(),
                            credentials.getSecurityToken());
                    Date expiration = DateUtil.addSeconds(new Date(), this.tempReadExpiredSeconds);
                    URL url = oss.generatePresignedUrl(bucket, path, expiration);
                    return url.toString() + paramString;
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }

}
