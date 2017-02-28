package org.truenewx.support.unstructured.core;

import org.truenewx.core.model.UserIdentity;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;

/**
 * 非结构化存储授权方针
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizePolicy<T extends Enum<T>, U extends UserIdentity> {

    T getType();

    UnstructuredProvider getProvider();

    String getBucket();

    String getPath(U user, String filename);

    boolean isPublicReadable();

    boolean isReadable(U user, String path);

    boolean isWritable(U user, String path);

}
