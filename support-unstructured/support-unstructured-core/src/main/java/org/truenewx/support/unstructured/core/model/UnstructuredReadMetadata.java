package org.truenewx.support.unstructured.core.model;

/**
 * 非结构化存储的资源读取元信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnstructuredReadMetadata {

    private String readUrl;
    private UnstructuredStorageMetadata storageMetadata;

    public UnstructuredReadMetadata(final String readUrl,
            final UnstructuredStorageMetadata storageMetadata) {
        this.readUrl = readUrl;
        this.storageMetadata = storageMetadata;
    }

    public void setReadUrl(final String readUrl) {
        this.readUrl = readUrl;
    }

    public String getReadUrl() {
        return this.readUrl;
    }

    public String getFilename() {
        return this.storageMetadata == null ? null : this.storageMetadata.getFilename();
    }

    public long getSize() {
        return this.storageMetadata == null ? 0 : this.storageMetadata.getSize();
    }

    public long getLastModifiedTime() {
        return this.storageMetadata == null ? 0 : this.storageMetadata.getLastModifiedTime();
    }

    public String getMimeType() {
        return this.storageMetadata == null ? null : this.storageMetadata.getMimeType();
    }

}
