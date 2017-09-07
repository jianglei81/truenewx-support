package org.truenewx.support.unstructured.core.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.core.io.CompositeOutputStream;
import org.truenewx.core.util.IOUtil;
import org.truenewx.core.util.StringUtil;
import org.truenewx.support.unstructured.core.UnstructuredAccessor;

/**
 * 本地的非结构化存储访问器
 *
 * @author jianglei
 *
 */
public class LocalUnstructuredAccessor implements UnstructuredAccessor {

    private File root;
    private UnstructuredAccessor remoteAccessor;
    private Executor executor;

    public LocalUnstructuredAccessor(final String root) {
        final File file = new File(root);
        if (!file.exists()) { // 目录不存在则创建
            file.mkdirs();
        } else { // 必须是个目录
            Assert.isTrue(file.isDirectory());
        }
        Assert.isTrue(file.canRead() && file.canWrite());

        this.root = file;
    }

    /**
     * @param remoteAccessor
     *            远程访问器
     */
    public void setRemoteAccessor(final UnstructuredAccessor remoteAccessor) {
        this.remoteAccessor = remoteAccessor;
    }

    /**
     * @param executor
     *            线程执行器
     */
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public void write(final String bucket, final String path, final InputStream in)
            throws IOException {
        // 先上传内容到一个新建的临时文件中
        final File tempFile = createTempFile(bucket, path);
        final FileOutputStream out = new FileOutputStream(tempFile);
        IOUtil.writeAll(in, out);
        out.close();

        // 然后删除原文件，修改临时文件名为原文件名
        final File file = getStoreFile(bucket, path);
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);

        // 写至远程服务器
        writeToRemote(bucket, path, file);
    }

    private File getStoreFile(final String bucket, final String path) {
        final String relativePath = standardize(bucket) + standardize(path);
        final File file = new File(this.root, relativePath);
        file.mkdirs(); // 确保目录存在
        return file;
    }

    private File createTempFile(final String bucket, final String path) throws IOException {
        // 形如：${正式文件名}_${32位UUID}.temp;
        final String relativePath = standardize(bucket) + standardize(path) + Strings.UNDERLINE
                + StringUtil.uuid32() + Strings.DOT + "temp";
        final File file = new File(this.root, relativePath);
        file.mkdirs(); // 确保目录存在
        file.createNewFile(); // 创建新文件以写入内容
        file.setWritable(true);
        return file;
    }

    private String standardize(String path) {
        // 必须以斜杠开头，不能以斜杠结尾
        if (!path.startsWith(Strings.SLASH)) {
            path = Strings.SLASH + path;
        }
        if (path.endsWith(Strings.SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void writeToRemote(final String bucket, final String path, final File file) {
        if (this.executor != null && this.remoteAccessor != null) {
            this.executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final InputStream in = new FileInputStream(file);
                        LocalUnstructuredAccessor.this.remoteAccessor.write(bucket, path, in);
                        in.close();
                    } catch (final IOException e) {
                        LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    }
                }
            });
        }
    }

    @Override
    public void read(final String bucket, final String path, final OutputStream out)
            throws IOException {
        final File file = getStoreFile(bucket, path);
        if (!file.exists()) { // 如果文件不存在，则需要从远程服务器读取内容，并缓存到本地文件
            if (this.remoteAccessor != null) {
                file.createNewFile();
                final OutputStream fileOut = new FileOutputStream(file);
                this.remoteAccessor.read(bucket, path, new CompositeOutputStream(out, fileOut));
                fileOut.close();
            }
        } else {
            final InputStream in = new FileInputStream(file);
            IOUtil.writeAll(in, out);
            in.close();
        }
    }

}
