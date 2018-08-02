package org.truenewx.support.sms.http;

import java.util.Map;

import org.truenewx.core.Strings;

/**
 * 抽象的HTTP短信发送策略
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractHttpSmsSendStrategy implements HttpSmsSendStrategy {

    private String url;
    private String requestMethod = "POST";
    private String encoding = Strings.ENCODING_UTF8;
    protected Map<String, Object> defaultParams;

    @Override
    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public String getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(final String requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setDefaultParams(final Map<String, Object> defaultParams) {
        this.defaultParams = defaultParams;
    }

}
