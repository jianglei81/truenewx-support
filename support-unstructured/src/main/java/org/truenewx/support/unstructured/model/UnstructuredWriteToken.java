package org.truenewx.support.unstructured.model;

/**
 * 非结构化存储写权限令牌
 *
 * @author jianglei
 *
 */
public class UnstructuredWriteToken {

    /**
     * 服务商
     */
    private UnstructuredProvider provider;

    /**
     * 授权账号
     */
    private UnstructuredAccount account;

    /**
     * 存储主机路径
     */
    private String host;

    /**
     * 存储桶名称
     */
    private String bucket;

    /**
     * 文件路径
     */
    private String path;

    /**
     * @return 服务商
     */
    public UnstructuredProvider getProvider() {
        return this.provider;
    }

    /**
     * @param provider
     *            服务商
     */
    public void setProvider(final UnstructuredProvider provider) {
        this.provider = provider;
    }

    /**
     * @return 授权账号
     */
    public UnstructuredAccount getAccount() {
        return this.account;
    }

    /**
     * @param account
     *            授权账号
     */
    public void setAccount(final UnstructuredAccount account) {
        this.account = account;
    }

    /**
     * @return 存储主机路径
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @param host
     *            存储主机路径
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * @return
     */
    public String getBucket() {
        return this.bucket;
    }

    /**
     * @param bucket
     */
    public void setBucket(final String bucket) {
        this.bucket = bucket;
    }

    /**
     * @return
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @param path
     */
    public void setPath(final String path) {
        this.path = path;
    }

}
