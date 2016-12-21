package org.truenewx.support.unstructured;

import java.io.Serializable;

import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储授权工厂
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizeFactory<T extends Enum<T>, K extends Serializable> {

    UnstructuredWriteToken authorize(T authorizeType, K userId);

}
