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
    private String thumbnailReadUrl;

    public UploadResult(String filename, String storageUrl, String readUrl,
            String thumbnailReadUrl) {
        this.filename = filename;
        this.storageUrl = storageUrl;
        this.readUrl = readUrl;
        this.thumbnailReadUrl = thumbnailReadUrl;
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

    public String getThumbnailReadUrl() {
        return this.thumbnailReadUrl;
    }
}
