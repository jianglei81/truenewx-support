package org.truenewx.support.unstructured.core.local;

import org.truenewx.core.Strings;
import org.truenewx.support.unstructured.core.UnstructuredAuthorizer;
import org.truenewx.support.unstructured.core.model.UnstructuredAccessToken;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

/**
 * 本地的非结构化存储授权器
 *
 * @author jianglei
 *
 */
public class LocalUnstructuredAuthorizer implements UnstructuredAuthorizer {

    private String host;
    private String region;

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.OWN;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    /**
     * 设置访问区域
     *
     * @param region
     *            访问区域，此处为上传和下载Controller的action路径
     */
    public void setRegion(final String region) {
        this.region = region;
    }

    @Override
    public String standardizePath(String path) {
        // 必须以斜杠开头，不能以斜杠结尾
        if (!path.startsWith(Strings.SLASH)) {
            path = Strings.SLASH + path;
        }
        if (path.endsWith(Strings.SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        return null;
    }

    @Override
    public UnstructuredAccessToken authorizePrivateWrite(final String userKey, final String bucket,
            final String path) {
        return new UnstructuredAccessToken();
    }

    @Override
    public void authorizePublicRead(final String bucket, final String path) {
    }

    @Override
    public String getReadHttpUrl(final String userKey, final String bucket, final String path) {
        // 形如：http://${host}/${region}/${bucket}/${path}
        final StringBuffer url = new StringBuffer("http://").append(getHost()).append(Strings.SLASH)
                .append(getRegion()).append(Strings.SLASH).append(bucket).append(Strings.SLASH)
                .append(path);
        return url.toString();
    }

}
