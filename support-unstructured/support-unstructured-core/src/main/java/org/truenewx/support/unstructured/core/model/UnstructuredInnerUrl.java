package org.truenewx.support.unstructured.core.model;

import org.truenewx.core.Strings;

/**
 * 非结构化存储内部URL，包含了内部URL的转换逻辑
 *
 * @author jianglei
 *
 */
public class UnstructuredInnerUrl {
    private UnstructuredProvider provider;
    private String bucket;
    private String path;

    public UnstructuredInnerUrl(final UnstructuredProvider provider, final String bucket,
            final String path) {
        this.provider = provider;
        this.bucket = bucket;
        this.path = path;
    }

    public UnstructuredInnerUrl(final String innerUrl) {
        int index1 = innerUrl.indexOf("://");
        if (index1 > 0) {
            final String protocol = innerUrl.substring(0, index1);
            this.provider = UnstructuredProvider.valueOf(protocol.toUpperCase());

            index1 += 3;
            final int index2 = innerUrl.indexOf(Strings.SLASH, index1);
            if (index2 > 0) {
                this.bucket = innerUrl.substring(index1, index2);
                this.path = innerUrl.substring(index2);
            }
        }
    }

    public UnstructuredProvider getProvider() {
        return this.provider;
    }

    public String getBucket() {
        return this.bucket;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isValid() {
        return this.provider != null && this.bucket != null && this.path != null;
    }

    @Override
    public String toString() {
        if (!isValid()) { // 无效则返回null
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
