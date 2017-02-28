package org.truenewx.support.unstructured.core.aliyun;

import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.support.unstructured.core.UnstructuredAccessor;

/**
 * 阿里云的非结构化存储访问器
 * 
 * @author jianglei
 *
 */
public class AliyunUnstructuredAccessor implements UnstructuredAccessor {

    private AliyunAccount account;

    public void setAccount(final AliyunAccount account) {
        this.account = account;
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
