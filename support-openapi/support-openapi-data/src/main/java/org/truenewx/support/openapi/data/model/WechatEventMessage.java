package org.truenewx.support.openapi.data.model;

/**
 * 微信开放接口事件消息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class WechatEventMessage extends WechatMessage {

    private WechatEventType eventType;

    public WechatEventMessage(WechatEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public WechatMessageType getType() {
        return WechatMessageType.EVENT;
    }

    public WechatEventType getEventType() {
        return this.eventType;
    }

}
