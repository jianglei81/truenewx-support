package org.truenewx.support.unstructured.model;

/**
 * 非结构化操作账号
 *
 * @author jianglei
 *
 */
public class UnstructuredAccount {
    private String id;
    private String secret;

    /**
     * @return 编号
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     *            编号
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return 密钥
     */
    public String getSecret() {
        return this.secret;
    }

    /**
     * @param secret
     *            密钥
     */
    public void setSecret(final String secret) {
        this.secret = secret;
    }

}
