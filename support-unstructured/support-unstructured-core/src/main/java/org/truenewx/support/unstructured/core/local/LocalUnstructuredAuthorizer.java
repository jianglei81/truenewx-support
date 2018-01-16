package org.truenewx.support.unstructured.core.local;

import org.truenewx.core.Strings;
import org.truenewx.support.unstructured.core.UnstructuredAuthorizer;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

/**
 * 本地的非结构化存储授权器
 *
 * @author jianglei
 *
 */
public class LocalUnstructuredAuthorizer implements UnstructuredAuthorizer {

    private String downloadPathPrefix = "/unstructured/dl";

    /**
     *
     * @param downloadPathPrefix
     *            资源下载路径前缀
     */
    public void setDownloadPathPrefix(final String downloadPathPrefix) {
        this.downloadPathPrefix = downloadPathPrefix;
    }

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.OWN;
    }

    @Override
    public void authorizePublicRead(final String bucket, final String path) {
        // 本地资源本身没有权限限制，权限由Policy进行限制和判断
    }

    @Override
    public String getReadUrl(final String userKey, final String bucket, final String path) {
        // 形如：/${downloadPathPrefix}/${bucket}/${path}，本地地址交由Controller完成上下文根路径的拼装
        final StringBuffer url = new StringBuffer(this.downloadPathPrefix).append(Strings.SLASH)
                .append(bucket).append(Strings.SLASH).append(path);
        return url.toString();
    }

}
