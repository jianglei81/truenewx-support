package org.truenewx.support.unstructured.web.resolver;

import org.truenewx.core.exception.BusinessException;

/**
 * 非结构化存储的资源读取地址解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface UnstructuredReadUrlResolver {

    /**
     * 根据内部存储地址获取外部读取地址
     *
     * @param storageUrl
     *            内部存储地址
     * @return 外部读取地址
     * @throws BusinessException
     *             如果对指定资源没有读取权限
     */
    String getReadUrl(String storageUrl) throws BusinessException;

}
