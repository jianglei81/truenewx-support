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

}