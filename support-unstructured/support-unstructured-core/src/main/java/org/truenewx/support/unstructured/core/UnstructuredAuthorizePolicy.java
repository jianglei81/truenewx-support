package org.truenewx.support.unstructured.core;

import java.util.Map;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;
import org.truenewx.support.unstructured.core.model.UnstructuredUploadLimit;

/**
 * 非结构化存储授权方针
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizePolicy<T extends Enum<T>, U> {

    T getType();

    UnstructuredProvider getProvider();

    /**
     * 指定是否需要本地存储，默认为true
     *
     * @return 是否本地存储
     */
    default boolean isStoreLocally() {
        return true;
    }

    /**
     * 获取在当前方针下，指定用户上传文件的限制条件
     *
     * @param user 用户标识
     * @return 指定用户上传文件的限制条件
     */
    UnstructuredUploadLimit getUploadLimit(U user);

    /**
     * 获取存储桶名，存储桶名要求全系统唯一，或者与其它方针的存储桶相同时，下级存放路径不同
     *
     * @return 存储桶名
     */
    String getBucket();

    /**
     * 是否将上传文件的MD5码作为文件名
     *
     * @return 是否将上传文件的MD5码作为文件名
     */
    default boolean isMd5AsFilename() {
        return false;
    }

    String getPath(U user, String filename) throws BusinessException;

    default boolean isPublicReadable() {
        return false;
    }

    boolean isReadable(U user, String path);

    boolean isWritable(U user, String path);

    /**
     * 获取缩略图读取参数集，仅在文件为图片时有效，返回空时表示不支持缩略图
     *
     * @return 缩略图读取参数集
     */
    default Map<String, String> getThumbnailParameters() {
        return null;
    }

}
