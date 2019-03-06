package org.truenewx.support.unstructured.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.support.unstructured.core.model.UnstructuredReadMetadata;
import org.truenewx.support.unstructured.core.model.UnstructuredUploadLimit;

/**
 * 非结构化存储服务模版
 *
 * @author jianglei
 * @param <T>
 *            授权类型
 * @param <U>
 *            用户标识类型
 */
public interface UnstructuredServiceTemplate<T extends Enum<T>, U> {

    /**
     * 获取在当前方针下，指定用户上传指定授权类型文件的限制条件
     *
     * @param authorizeType
     *            授权类型
     * @param user
     *            用户标识
     * @return 指定用户上传指定授权类型文件的限制条件
     * @throws BusinessException
     *             如果授权类型无对应的方针
     */
    UnstructuredUploadLimit getUploadLimit(T authorizeType, U user) throws BusinessException;

    /**
     * 指定用户在指定授权类型下写文件
     *
     * @param authorizeType
     *            授权类型
     * @param token TODO
     * @param user
     *            用户标识
     * @param filename
     *            文件名
     * @param in
     *            输入流
     * @return 写好的文件的内部存储URL
     * @throws BusinessException
     *             如果指定用户对指定资源没有写权限
     * @throws IOException
     *             如果写的过程中出现错误
     */
    String write(T authorizeType, String token, U user, String filename, InputStream in)
            throws BusinessException, IOException;

    /**
     * 指定用户获取指定内部存储URL对应的外部读取URL
     *
     * @param user
     *            用户标识
     * @param storageUrl
     *            内部存储URL
     * @param thumbnail TODO
     * @return 外部读取URL
     * @throws BusinessException
     *             如果指定用户对指定资源没有读取权限
     */
    String getReadUrl(U user, String storageUrl, boolean thumbnail) throws BusinessException;

    /**
     * 获取指定资源的读取元信息
     *
     * @param user
     *            用户标识
     * @param storageUrl
     *            资源的存储路径
     * @return 指定资源的读取元信息
     * @throws BusinessException
     *             如果指定用户对指定资源没有读取权限
     */
    UnstructuredReadMetadata getReadMetadata(U user, String storageUrl) throws BusinessException;

    /**
     * 获取指定资源的最后修改时间
     *
     * @param user
     *            用户标识
     * @param bucket
     *            存储桶名
     * @param path
     *            存储路径
     *
     * @return 最后修改时间毫秒数，指定资源不存在时返回0
     * @throws BusinessException
     *             如果指定用户对指定资源没有读取权限
     */
    long getLastModifiedTime(U user, String bucket, String path) throws BusinessException;

    /**
     * 指定用户读取指定路径的文件内容到指定输出流中
     *
     * @param user
     *            用户标识
     * @param bucket
     *            存储桶名
     * @param path
     *            存储路径
     * @param out
     *            输出流
     * @throws BusinessException
     *             如果指定用户对指定资源没有读取权限
     * @throws IOException
     *             如果读的过程中出现错误
     */
    void read(U user, String bucket, String path, OutputStream out)
            throws BusinessException, IOException;

}
