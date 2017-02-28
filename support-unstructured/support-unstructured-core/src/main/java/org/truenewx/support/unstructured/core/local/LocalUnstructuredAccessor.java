package org.truenewx.support.unstructured.core.local;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import org.springframework.core.io.Resource;
import org.truenewx.support.unstructured.core.UnstructuredAccessor;

/**
 * 本地的非结构化存储访问器
 *
 * @author jianglei
 *
 */
public class LocalUnstructuredAccessor implements UnstructuredAccessor {

    private Resource root;
    private UnstructuredAccessor remoteAccessor;
    private Executor executor;

    /**
     * @param root
     *            存放文件的根目录
     */
    public void setRoot(final Resource root) {
        this.root = root;
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
    public void write(final String bucket, final String path, final InputStream in) {
        // TODO Auto-generated method stub

    }

    @Override
    public void read(final String bucket, final String path, final OutputStream out) {
        // TODO Auto-generated method stub

    }

}
