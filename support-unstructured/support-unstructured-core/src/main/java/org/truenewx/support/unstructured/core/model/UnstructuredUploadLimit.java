package org.truenewx.support.unstructured.core.model;

/**
 * 非结构化存储上传限制
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnstructuredUploadLimit {

    private long capacity;
    private String[] extensions;

    public UnstructuredUploadLimit(final long capacity, final String... extensions) {
        this.capacity = capacity;
        this.extensions = extensions;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public String[] getExtensions() {
        return this.extensions;
    }

}
