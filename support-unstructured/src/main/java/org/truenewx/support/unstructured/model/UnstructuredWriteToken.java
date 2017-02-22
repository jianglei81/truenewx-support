package org.truenewx.support.unstructured.model;

import org.truenewx.core.Strings;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.util.StringUtil;

/**
 * 非结构化存储写权限令牌
 *
 * @author jianglei
 *
 */
public class UnstructuredWriteToken extends UnstructuredAccess {

    @Caption("唯一标识一个令牌的32位UUID")
    private String uuid;

    @Caption("服务商")
    private UnstructuredProvider provider;

    @Caption("地区")
    private String region;

    @Caption("存储主机路径")
    private String host;

    @Caption("存储桶名称")
    private String bucket;

    @Caption("资源相对路径")
    private String path;

    @Caption("是否公开可读")
    private boolean publicReadable;

    public UnstructuredWriteToken() {
        this.uuid = StringUtil.uuid32();
    }

    /**
     * @return 32位UUID
     */
    public String getUuid() {
        return this.uuid;
    }

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

    public String getInnerUrl() {
        if (this.provider == null || this.bucket == null || this.path == null) { // 三者均不能为null
            return null;
        }
        // 形如：${proivder}://${bucket}/${path}
        final StringBuffer url = new StringBuffer(this.provider.name().toLowerCase()).append("://")
                .append(this.bucket);
        if (!this.path.startsWith(Strings.SLASH)) {
            url.append(Strings.SLASH);
        }
        url.append(this.path);
        return url.toString();
    }
}
