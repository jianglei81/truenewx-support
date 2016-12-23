package org.truenewx.support.unstructured;

import java.io.Serializable;

import org.truenewx.support.unstructured.model.UnstructuredProvider;

/**
 * 非结构化存储授权方针
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizePolicy<T extends Enum<T>, K extends Serializable> {

    T getType();

    UnstructuredProvider getProvider();

    String getUserKey(K userId);

    String getBucket(K userId);

    String getPath(K userId, String filename);

    boolean isPublicReadable(K userId);

}
