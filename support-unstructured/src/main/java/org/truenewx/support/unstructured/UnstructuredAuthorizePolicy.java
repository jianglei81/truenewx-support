package org.truenewx.support.unstructured;

import java.io.Serializable;

/**
 * 非结构化存储授权方针
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizePolicy<T extends Enum<T>, K extends Serializable> {

    T getType();

    String getUserKey(K userId);

    String getBucket(K userId);

    String getPath(K userId);

}
