package org.truenewx.support.unstructured.core.model;

import org.springframework.util.Assert;

import com.aliyun.oss.internal.Mimetypes;

/**
 * 非结构化存储上传限制
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnstructuredUploadLimit {

    private int number;
    private long capacity;
    private boolean rejectedExtension;
    private String[] extensions;
    private String[] mimeTypes;

    public UnstructuredUploadLimit(final int number, final long capacity,
            final String... extensions) {
        this(number, capacity, false, extensions);
    }

    public UnstructuredUploadLimit(final int number, final long capacity,
            final boolean rejectedExtension, final String... extensions) {
        Assert.isTrue(number >= 0, "number must be not less than 0");
        this.number = number;
        Assert.isTrue(capacity >= 0, "capacity must be not less than 0");
        this.capacity = capacity;
        this.rejectedExtension = rejectedExtension;
        this.extensions = extensions;
        this.mimeTypes = new String[extensions.length];
        final Mimetypes mimetypes = Mimetypes.getInstance();
        for (int i = 0; i < extensions.length; i++) {
            final String extension = "temp." + extensions[i];
            this.mimeTypes[i] = mimetypes.getMimetype(extension);
        }
    }

    public int getNumber() {
        return this.number;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public boolean isRejectedExtension() {
        return this.rejectedExtension;
    }

    public String[] getExtensions() {
        return this.extensions;
    }

    public String[] getMimeTypes() {
        return this.mimeTypes;
    }

}
