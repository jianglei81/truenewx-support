package org.truenewx.support.unstructured.core;

import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

/**
 * 非结构化存储服务提供商访问器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface UnstructuredProviderAccessor extends UnstructuredAccessor {

    UnstructuredProvider getProvider();

}
