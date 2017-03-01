package org.truenewx.support.unstructured.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 非结构化数据访问器
 *
 * @author jianglei
 *
 */
public interface UnstructuredAccessor {

    void write(String bucket, String path, InputStream in) throws IOException;

    void read(String bucket, String path, OutputStream out) throws IOException;

}
