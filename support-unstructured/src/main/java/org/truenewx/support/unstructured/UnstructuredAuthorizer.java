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
     * 完全授权，包括读写权限
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            文件路径
     * @return 授权后令牌，授权失败将返回null
     */
    UnstructuredWriteToken authorize(String userKey, String bucket, String path);

    /**
     * 只读授权
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            文件路径
     */
    void authorizeOnlyRead(String userKey, String bucket, String path);

    /**
     * 取消授权
     *
     * @param userKey
     *            用户唯一标识
     * @param bucket
     *            存储桶名称
     * @param path
     *            文件路径
     */
    void unauthorize(String userKey, String bucket, String path);

}
