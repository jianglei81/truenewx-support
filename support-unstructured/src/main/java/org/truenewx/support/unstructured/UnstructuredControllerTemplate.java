package org.truenewx.support.unstructured;

import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储授权控制器
 *
 * @author jianglei
 *
 */
public interface UnstructuredControllerTemplate<T extends Enum<T>> {

    UnstructuredWriteToken authorizePrivateWrite(final T authorizeType);

    void authorizePublicRead(final T authorizeType, final String filename);

    String getOuterUrl(final T authorizeType, String innerUrl, String protocol);
}
