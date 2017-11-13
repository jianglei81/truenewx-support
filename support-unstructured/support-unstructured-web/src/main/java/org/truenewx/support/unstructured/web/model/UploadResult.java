package org.truenewx.support.unstructured.web.model;

/**
 * 上传结果
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UploadResult {

    private String filename;
    private String storageUrl;
    private String readUrl;

    public UploadResult(final String filename, final String storageUrl, final String readUrl) {
        this.filename = filename;
        this.storageUrl = storageUrl;
        this.readUrl = readUrl;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getStorageUrl() {
        return this.storageUrl;
    }

    public String getReadUrl() {
        return this.readUrl;
    }
}
