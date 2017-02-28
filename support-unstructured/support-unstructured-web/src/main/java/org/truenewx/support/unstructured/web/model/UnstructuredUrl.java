package org.truenewx.support.unstructured.web.model;

/**
 * 非结构化存储访问URL
 *
 * @author jianglei
 *
 */
public class UnstructuredUrl {

    private String inner;
    private String outer;

    public UnstructuredUrl() {
    }

    public UnstructuredUrl(final String inner, final String outer) {
        this.inner = inner;
        this.outer = outer;
    }

    public String getInner() {
        return this.inner;
    }

    public void setInner(final String inner) {
        this.inner = inner;
    }

    public String getOuter() {
        return this.outer;
    }

    public void setOuter(final String outer) {
        this.outer = outer;
    }

}
