package org.truenewx.support.unstructured;

import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储授权器
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizer {

    /**
     * 指定用户获取指定资源的完全授权，包括读写权限
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            资源路径
     * @return 授权后令牌，授权失败将返回null
     */
    UnstructuredWriteToken authorize(String userKey, String bucket, String path);

    /**
     * 公开指定资源可匿名读取
     *
     * @param bucket
     *            存储桶名称
     * @param path
     *            资源路径
     */
    void authorizePublicRead(String bucket, String path);

    /**
     * 指定用户获取指定资源的只读授权，如果原有可写权限，将被取消
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            资源路径
     */
    void authorizeOnlyRead(String userKey, String bucket, String path);

    /**
     * 取消指定用户对指定资源的所有授权
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            资源路径
     */
    void unauthorize(String userKey, String bucket, String path);

}
