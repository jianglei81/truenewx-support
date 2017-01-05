package org.truenewx.support.unstructured.model;

import org.truenewx.core.annotation.Caption;

/**
 * 非结构化存储写权限令牌
 *
 * @author jianglei
 *
 */
public class UnstructuredWriteToken extends UnstructuredAccess {

    @Caption("服务商")
    private UnstructuredProvider provider;

    @Caption("存储主机路径")
    private String host;

    @Caption("存储桶名称")
    private String bucket;

    @Caption("资源相对路径")
    private String path;

    @Caption("资源内部URL")
    private String innerUrl;

    @Caption("是否公开可读")
    private boolean publicReadable;

    @Caption("地区")
    private String region;

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
     * @return 储存桶
     */
    public String getBucket() {
        return this.bucket;
    }

    /**
     * @param bucket
     *            储存桶
     */
    public void setBucket(final String bucket) {
        this.bucket = bucket;
    }

    /**
     * @return 资源相对路径
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @param path
     *            资源相对路径
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * @return 资源内部URL
     */
    public String getInnerUrl() {
        return this.innerUrl;
    }

    /**
     * @param innerUrl
     *            资源内部URL
     */
    public void setInnerUrl(final String innerUrl) {
        this.innerUrl = innerUrl;
    }

    /**
     * @return 是否公开可读
     */
    public boolean isPublicReadable() {
        return this.publicReadable;
    }

    /**
     * @param publicReadable
     *            是否公开可读
     */
    public void setPublicReadable(final boolean publicReadable) {
        this.publicReadable = publicReadable;
    }

    /**
     *
     * @author liaozhan
     *
     * @return 地区
     */
    public String getRegion() {
        return this.region;
    }

    /**
     *
     * @author liaozhan
     *
     * @param region
     *            地区
     */
    public void setRegion(final String region) {
        this.region = region;
    }
}
