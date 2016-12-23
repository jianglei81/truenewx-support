package org.truenewx.support.unstructured;

import org.truenewx.support.unstructured.model.UnstructuredProvider;

/**
 * 非结构化存储资源URL构建器
 *
 * @author jianglei
 *
 */
public interface UnstructuredUrlBuilder {

    UnstructuredProvider getProvider();

    String buildUrl(String protocol, String bucket, String path);

}
