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
     * 根据内部存储地址获取外部读取地址<br/>
     * 可能有三种格式的结果：<br/>
     * 1./${contextPath}开头，相对当前主机地址的相对路径，调用者如想获得绝对地址需加上当前主机地址；<br/>
     * 2.http://或https://开头，包含主机地址且指定了访问协议的绝对地址；<br/>
     * 3.//开头，包含主机地址但不指定访问协议，允许使用http和https中的任意一种协议访问
     *
     * @param storageUrl
     *            内部存储地址
     * @param thumbnail
     *            是否缩略图
     * @return 外部读取地址
     * @throws BusinessException
     *             如果对指定资源没有读取权限
     */
    String getReadUrl(String storageUrl, boolean thumbnail) throws BusinessException;

}
