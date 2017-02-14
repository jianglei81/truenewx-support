package org.truenewx.support.unstructured.model;

/**
 * 非结构化存储访问参数
 *
 * @author jianglei
 *
 */
public class UnstructuredAccess {

    /**
     * 授权访问id
     */
    private String accessId;
    /**
     * 授权访问密钥
     */
    private String accessSecret;
    /**
     * 临时授权令牌
     */
    private String tempToken;

    public UnstructuredAccess() {
    }

    public UnstructuredAccess(final String accessId, final String accessSecret) {
        this.accessId = accessId;
        this.accessSecret = accessSecret;
    }

    /**
     * @return 授权访问id
     */
    public String getAccessId() {
        return this.accessId;
    }

    /**
     * @param accessId
     *            授权访问id
     */
    public void setAccessId(final String accessId) {
        this.accessId = accessId;
    }

    /**
     * @return 授权访问密钥
     */
    public String getAccessSecret() {
        return this.accessSecret;
    }

    /**
     * @param accessSecret
     *            授权访问密钥
     */
    public void setAccessSecret(final String accessSecret) {
        this.accessSecret = accessSecret;
    }

    /**
     * @return 临时授权令牌
     */
    public String getTempToken() {
        return this.tempToken;
    }

    /**
     * @param tempToken
     *            临时授权令牌
     */
    public void setTempToken(final String tempToken) {
        this.tempToken = tempToken;
    }

}