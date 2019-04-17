package org.truenewx.support.openapi.core.listener;

import org.truenewx.support.openapi.core.model.WeixinOpenApiMessage;
import org.truenewx.support.openapi.core.model.WeixinOpenApiMessageType;

/**
 * 微信开放接口消息处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface WeixinOpenApiMessageHandler {

    /**
     *
     * @return 侦听的消息类型集合
     */
    WeixinOpenApiMessageType[] getMessageTypes();

    /**
     * 当接收到指定类型的消息时触发的处理方法
     *
     * @param message 消息
     */
    void handleMessage(WeixinOpenApiMessage message);

}
