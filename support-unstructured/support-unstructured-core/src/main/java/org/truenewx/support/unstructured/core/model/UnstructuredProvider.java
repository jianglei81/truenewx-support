package org.truenewx.support.unstructured.core.model;

import org.truenewx.core.annotation.Caption;

/**
 * 非结构化存储服务提供商
 *
 * @author jianglei
 *
 */
public enum UnstructuredProvider {

    @Caption("阿里云")
    ALIYUN,

    @Caption("亚马逊云")
    AWS,

    @Caption("自有")
    OWN;

}
