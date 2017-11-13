package org.truenewx.support.unstructured.core.model;

import org.springframework.util.Assert;

/**
 * 非结构化存储上传限制
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnstructuredUploadLimit {

    private int number;
    private long capacity;
    private String[] extensions;

    public UnstructuredUploadLimit(final int number, final long capacity,
            final String... extensions) {
        Assert.isTrue(number > 0, "number must be larger than 0");
        this.number = number;
        Assert.isTrue(capacity > 0, "capacity must be larger than 0");
        this.capacity = capacity;
        this.extensions = extensions;
    }

    public int getNumber() {
        return this.number;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public String[] getExtensions() {
        return this.extensions;
    }

}
