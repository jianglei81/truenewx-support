package org.truenewx.support.unstructured.core.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.core.io.AttachInputStream;
import org.truenewx.core.io.AttachOutputStream;
import org.truenewx.core.io.CompositeOutputStream;
import org.truenewx.core.util.IOUtil;
import org.truenewx.core.util.StringUtil;
import org.truenewx.support.unstructured.core.UnstructuredAccessor;
import org.truenewx.support.unstructured.core.model.UnstructuredStorageMetadata;

/**
 * 本地的非结构化存储访问器
 *
 * @author jianglei
 *
 */
public class LocalUnstructuredAccessor implements UnstructuredAccessor {

    private File root;
    private UnstructuredAccessor remoteAccessor;

    public LocalUnstructuredAccessor(String root) {
        File file = new File(root);
        if (!file.exists()) { // 目录不存在则创建
            file.mkdirs();
        } else { // 必须是个目录
            Assert.isTrue(file.isDirectory(), "root must be a directory");
        }
        Assert.isTrue(file.canRead() && file.canWrite(), "root can not read or write");

        this.root = file;
    }

    /**
     * @param remoteAccessor
     *            远程访问器
     */
    public void setRemoteAccessor(UnstructuredAccessor remoteAccessor) {
        if (remoteAccessor != this) { // 避免自己引用自己从而导致无限递归调用
            this.remoteAccessor = remoteAccessor;
        }
    }

    @Override
    public void write(String bucket, String path, String filename, InputStream in)
            throws IOException {
        // 先上传内容到一个新建的临时文件中，以免在处理过程中原文件被读取
        File tempFile = createTempFile(bucket, path);
        OutputStream out = new AttachOutputStream(new FileOutputStream(tempFile), filename);
        IOUtil.writeAll(in, out);
        out.close();

        // 然后删除原文件，修改临时文件名为原文件名
        File file = getStorageFile(bucket, path);
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);

        // 写至远程服务器
        writeToRemote(bucket, path, filename, file);
    }

    private File createTempFile(String bucket, String path) throws IOException {
        // 形如：${正式文件名}_${32位UUID}.temp;
        String relativePath = standardize(bucket) + standardize(path) + Strings.UNDERLINE
                + StringUtil.uuid32() + Strings.DOT + "temp";
        File file = new File(this.root, relativePath);
        ensureDirs(file);
        file.createNewFile(); // 创建新文件以写入内容
        file.setWritable(true);
        return file;
    }

    /**
     * 确保指定文件的所属目录存在
     *
     * @param file
     *            文件
     */
    private void ensureDirs(File file) {
        File parent = file.getParentFile();
        // 上级目录路径中可能已经存在一个同名文件，导致目录无法创建，此时修改该文件的名称
        while (parent != null) {
            if (parent.exists() && !parent.isDirectory()) {
                parent.renameTo(new File(parent.getAbsolutePath() + ".temp"));
                break;
            }
            parent = parent.getParentFile();
        }
        file.getParentFile().mkdirs(); // 确保目录存在
    }

    private File getStorageFile(String bucket, String path) {
        String relativePath = standardize(bucket) + standardize(path);
        File file = new File(this.root, relativePath);
        ensureDirs(file);
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

    private void writeToRemote(String bucket, String path, String filename, File file)
            throws IOException {
        if (this.remoteAccessor != null) {
            // 上传至远程的文件内容不包含附加信息
            InputStream in = new AttachInputStream(new FileInputStream(file));
            this.remoteAccessor.write(bucket, path, filename, in);
            in.close();
        }
    }

    @Override
    public UnstructuredStorageMetadata getStorageMetadata(String bucket, String path) {
        try {
            File file = getStorageFile(bucket, path);
            if (file.exists()) {
                AttachInputStream in = new AttachInputStream(new FileInputStream(file));
                String filename = in.readAttachement();
                int size = in.available(); // 读取完附加信息后，输入流剩余的长度即为资源大小
                in.close();
                return new UnstructuredStorageMetadata(filename, size, file.lastModified());
            } else if (this.remoteAccessor != null) {
                return this.remoteAccessor.getStorageMetadata(bucket, path);
            }
        } catch (Exception e) {
            // 忽略所有异常
        }
        return null;
    }

    @Override
    public long getLastModifiedTime(String bucket, String path) {
        try {
            File file = getStorageFile(bucket, path);
            if (file.exists()) {
                return file.lastModified();
            } else if (this.remoteAccessor != null) {
                return this.remoteAccessor.getLastModifiedTime(bucket, path);
            }
        } catch (Exception e) {
            // 忽略所有异常
        }
        return 0;
    }

    @Override
    public void read(String bucket, String path, OutputStream out) throws IOException {
        File file = getStorageFile(bucket, path);
        if (!file.exists()) { // 如果文件不存在，则需要从远程服务器读取内容，并缓存到本地文件
            if (this.remoteAccessor != null) {
                UnstructuredStorageMetadata metadata = this.remoteAccessor
                        .getStorageMetadata(bucket, path);
                if (metadata != null) {
                    file.createNewFile();
                    String filename = metadata.getFilename();
                    OutputStream fileOut = new AttachOutputStream(new FileOutputStream(file),
                            filename);
                    this.remoteAccessor.read(bucket, path, new CompositeOutputStream(out, fileOut));
                    fileOut.close();
                }
            }
        } else {
            InputStream in = new AttachInputStream(new FileInputStream(file));
            IOUtil.writeAll(in, out);
            in.close();
        }
    }

}
