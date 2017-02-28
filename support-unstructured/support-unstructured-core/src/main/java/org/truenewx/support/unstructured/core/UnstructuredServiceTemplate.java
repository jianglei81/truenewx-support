package org.truenewx.support.unstructured.core;

import java.io.InputStream;
import java.io.OutputStream;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.UserIdentity;
import org.truenewx.support.unstructured.core.model.UnstructuredWriteToken;

/**
 * 非结构化存储服务模版
 *
 * @author jianglei
 * @param <T>
 *            授权类型
 * @param <U>
 *            用户标识类型
 */
public interface UnstructuredServiceTemplate<T extends Enum<T>, U extends UserIdentity> {

    /**
     * 指定用户获取指定授权类型资源的写权限
     *
     * @param authorizeType
     *            授权类型
     * @param user
     *            用户标识
     * @return 写权限令牌
     */
    UnstructuredWriteToken authorizePrivateWrite(T authorizeType, U user);

    /**
     * 公开指定用户在指定授权类型资源中指定文件的读取授权，可供匿名用户访问
     *
     * @param authorizeType
     *            授权类型
     * @param user
     *            用户标识
     * @param filename
     *            文件名
     */
    void authorizePublicRead(T authorizeType, U user, String filename);

    /**
     * 指定用户获取指定授权类型资源URL的外部访问URL
     *
     * @param authorizeType
     *            授权类型
     * @param user
     *            用户标识
     * @param innerUrl
     *            内部URL
     * @param protocol
     *            外部访问协议，取值：http、https、空
     * @return 外部访问URL
     */
    String getOuterUrl(T authorizeType, U user, String innerUrl, String protocol);

    /**
     * 指定用户在指定授权类型下写文件
     *
     * @param authorizeType
     *            授权类型
     * @param user
     *            用户标识
     * @param filename
     *            文件名
     * @param in
     *            输入流
     * @return 写好的文件的内部访问URL
     * @throws BusinessException
     *             如果没有写权限
     */
    String write(T authorizeType, U user, String filename, InputStream in) throws BusinessException;

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
     *             如果没有读权限
     */
    void read(U user, String bucket, String path, OutputStream out) throws BusinessException;

}
