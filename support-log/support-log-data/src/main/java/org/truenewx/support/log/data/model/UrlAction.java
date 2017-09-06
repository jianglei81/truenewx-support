package org.truenewx.support.log.data.model;

import java.util.Map;

import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * URL访问操作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UrlAction extends Action {

    private String url;
    private String method;
    private Map<String, Object> params;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(final Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String getType() {
        return "URL";
    }

    @Override
    public int hashCode() {
        final Object[] array = { this.url, this.method, this.params };
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UrlAction other = (UrlAction) obj;
        return PredEqual.INSTANCE.apply(this.url, other.url)
                && PredEqual.INSTANCE.apply(this.method, other.method)
                && PredEqual.INSTANCE.apply(this.params, other.params);
    }

}
