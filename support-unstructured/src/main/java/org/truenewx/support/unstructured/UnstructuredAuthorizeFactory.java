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

    /**
     * 指定用户获取指定授权类型资源的读写权限
     *
     * @param authorizeType
     *            授权类型
     * @param userId
     *            用户id
     * @return 写权限令牌
     */
    UnstructuredWriteToken authorize(T authorizeType, K userId);

    /**
     * 公开指定用户在指定授权类型资源中指定文件的读取授权，可供匿名用户访问
     *
     * @param authorizeType
     *            授权类型
     * @param userId
     *            用户id
     * @param filename
     *            文件名
     */
    void authorizePublicRead(T authorizeType, K userId, String filename);

}
