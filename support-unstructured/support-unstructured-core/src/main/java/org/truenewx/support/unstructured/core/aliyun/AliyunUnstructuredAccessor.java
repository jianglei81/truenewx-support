package org.truenewx.support.unstructured.core.aliyun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.core.util.IOUtil;
import org.truenewx.support.unstructured.core.UnstructuredProviderAccessor;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;
import org.truenewx.support.unstructured.core.model.UnstructuredStorageMetadata;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * 阿里云的非结构化存储访问器
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAccessor implements UnstructuredProviderAccessor {

    private AliyunAccount account;

    public AliyunUnstructuredAccessor(AliyunAccount account) {
        this.account = account;
    }

    @Override
    public UnstructuredProvider getProvider() {
        return UnstructuredProvider.ALIYUN;
    }

    @Override
    public void write(String bucket, String path, String filename, InputStream in)
            throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.getUserMetadata().put("filename", filename);
        this.account.getOssClient().putObject(bucket, path, in, objectMetadata);
    }

    @Override
    public UnstructuredStorageMetadata getStorageMetadata(String bucket, String path) {
        try {
            ObjectMetadata objectMetadata = this.account.getOssClient().getObjectMetadata(bucket,
                    path);
            String filename = objectMetadata.getUserMetadata().get("filename");
            return new UnstructuredStorageMetadata(filename, objectMetadata.getContentLength(),
                    objectMetadata.getLastModified().getTime());
        } catch (Exception e) {
            // 忽略所有异常
            return null;
        }
    }

    @Override
    public long getLastModifiedTime(String bucket, String path) {
        try {
            return this.account.getOssClient().getObjectMetadata(bucket, path).getLastModified()
                    .getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean read(String bucket, String path, OutputStream out) throws IOException {
        try {
            InputStream in = this.account.getOssClient().getObject(bucket, path).getObjectContent();
            IOUtil.writeAll(in, out);
            in.close();
            return true;
        } catch (IOException e) {
            throw e;
        } catch (ClientException e) {
            return false;
        }
    }

}
