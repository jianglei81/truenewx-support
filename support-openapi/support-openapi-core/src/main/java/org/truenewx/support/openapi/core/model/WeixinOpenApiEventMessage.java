package org.truenewx.support.openapi.core.model;

/**
 * 微信开放接口事件消息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class WeixinOpenApiEventMessage extends WeixinOpenApiMessage {

    private WeixinOpenApiEventType eventType;

    public WeixinOpenApiEventMessage(WeixinOpenApiEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public WeixinOpenApiMessageType getType() {
        return WeixinOpenApiMessageType.EVENT;
    }

    public WeixinOpenApiEventType getEventType() {
        return this.eventType;
    }

}
