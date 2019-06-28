package org.truenewx.support.openapi.data.model;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 微信应用类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum WechatAppType {

    @Caption("小程序")
    @EnumValue("M")
    MP,

    @Caption("公众号")
    @EnumValue("S")
    SA,

    @Caption("网站")
    @EnumValue("W")
    WEB;

}
