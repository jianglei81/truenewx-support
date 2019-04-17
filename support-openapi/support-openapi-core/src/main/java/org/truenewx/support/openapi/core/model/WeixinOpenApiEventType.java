package org.truenewx.support.openapi.core.model;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 微信开放接口事件类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum WeixinOpenApiEventType {

    @Caption("关注")
    @EnumValue("S")
    SUBSCRIBE,

    @Caption("取消关注")
    @EnumValue("U")
    UNSUBSCRIBE,

    @Caption("上报地理位置")
    @EnumValue("L")
    LOCATION,

    @Caption("菜单点击")
    @EnumValue("C")
    CLICK,

    @Caption("链接跳转")
    @EnumValue("V")
    VIEW;

}
