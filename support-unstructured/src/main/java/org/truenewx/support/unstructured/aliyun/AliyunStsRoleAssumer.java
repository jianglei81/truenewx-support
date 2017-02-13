package org.truenewx.support.unstructured.aliyun;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials;

/**
 * 阿里云STS临时角色假扮器
 *
 * @author jianglei
 *
 */
public class AliyunStsRoleAssumer {

    private String roleArn;
    private long durationSeconds = 60 * 30l;
    private AliyunAccount account;

    /**
     *
     * @param accountId
     *            阿里云账号id
     * @param roleName
     *            sts临时角色名称
     */
    public AliyunStsRoleAssumer(final AliyunAccount account, final String roleName) {
        this.account = account;
        this.roleArn = "acs:ram::" + account.getAccountId() + ":role/" + roleName.toLowerCase();
    }

    public void setDurationSeconds(final long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Credentials assumeRole(final String roleSessionName, final String policyDocument) {
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(this.roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setPolicy(policyDocument);
        request.setDurationSeconds(this.durationSeconds);
        try {
            final AssumeRoleResponse response = this.account.getAcsClient().getAcsResponse(request);
            return response.getCredentials();
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
