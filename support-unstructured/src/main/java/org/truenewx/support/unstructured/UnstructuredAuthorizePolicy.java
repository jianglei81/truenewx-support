package org.truenewx.support.unstructured;

import java.io.Serializable;

import org.truenewx.support.unstructured.model.UnstructuredProvider;

/**
 * 非结构化存储授权方针
 *
 * @author jianglei
 *
 */
public interface UnstructuredAuthorizePolicy<AT extends Enum<AT>, UT extends Enum<UT>, UK extends Serializable> {

    AT getType();

    UnstructuredProvider getProvider();

    String getUserKey(UK userId);

    String getBucket(UK userId);

    String getPath(UK userId, String filename);

    boolean isPublicReadable();

    boolean isReadable(UT userType, UK userId, String path);

}
