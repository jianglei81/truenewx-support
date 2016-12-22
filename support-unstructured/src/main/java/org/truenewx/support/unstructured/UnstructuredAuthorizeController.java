package org.truenewx.support.unstructured;

import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储授权控制器
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizeController<T extends Enum<T>> {

    UnstructuredWriteToken authorizeWrite(final T authorizeType);

    void authorizePublicRead(final T authorizeType, final String filename);

}
