package org.truenewx.support.unstructured.core.aliyun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.core.util.IOUtil;
import org.truenewx.support.unstructured.core.UnstructuredAccessor;

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
    public void write(final String bucket, final String path, final InputStream in)
            throws IOException {
        this.account.getOssClient().putObject(bucket, path, in);
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
