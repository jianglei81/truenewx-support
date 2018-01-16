package org.truenewx.support.unstructured.core.aliyun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.core.util.IOUtil;
import org.truenewx.support.unstructured.core.UnstructuredAccessor;
import org.truenewx.support.unstructured.core.model.UnstructuredStorageMetadata;

import com.aliyun.oss.model.ObjectMetadata;

/**
 * 阿里云的非结构化存储访问器
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAccessor implements UnstructuredAccessor {

    private AliyunAccount account;

    public AliyunUnstructuredAccessor(final AliyunAccount account) {
        this.account = account;
    }

    @Override
    public void write(final String bucket, final String path, final String filename,
            final InputStream in) throws IOException {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.getUserMetadata().put("filename", filename);
        this.account.getOssClient().putObject(bucket, path, in, objectMetadata);
    }

    @Override
    public UnstructuredStorageMetadata getStorageMetadata(final String bucket, final String path) {
        try {
            final ObjectMetadata objectMetadata = this.account.getOssClient()
                    .getObjectMetadata(bucket, path);
            final String filename = objectMetadata.getUserMetadata().get("filename");
            return new UnstructuredStorageMetadata(filename, objectMetadata.getContentLength(),
                    objectMetadata.getLastModified().getTime());
        } catch (final Exception e) {
            // 忽略所有异常
            return null;
        }
    }

    @Override
    public long getLastModifiedTime(final String bucket, final String path) {
        try {
            return this.account.getOssClient().getObjectMetadata(bucket, path).getLastModified()
                    .getTime();
        } catch (final Exception e) {
            return 0;
        }
    }

    @Override
    public void read(final String bucket, final String path, final OutputStream out)
            throws IOException {
        final InputStream in = this.account.getOssClient().getObject(bucket, path)
                .getObjectContent();
        IOUtil.writeAll(in, out);
        in.close();
    }

}
