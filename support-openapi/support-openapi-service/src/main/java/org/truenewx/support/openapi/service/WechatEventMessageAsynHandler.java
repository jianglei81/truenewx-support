package org.truenewx.support.openapi.service;

import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.support.openapi.data.model.WechatEventMessage;
import org.truenewx.support.openapi.data.model.WechatEventType;
import org.truenewx.support.openapi.data.model.WechatMessage;
import org.truenewx.support.openapi.data.model.WechatMessageType;

/**
 * 微信开放接口事件消息异步处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatEventMessageAsynHandler implements WechatMessageAsynHandler {

    @Override
    public final WechatMessageType getMessageType() {
        return WechatMessageType.EVENT;
    }

    @Override
    @WriteTransactional
    public void handleMessage(WechatMessage message) {
        WechatEventMessage eventMessage = (WechatEventMessage) message;
        if (eventMessage.getEventType() == getEventType()) {
            doHandleMessage(eventMessage);
        }
    }

    protected abstract WechatEventType getEventType();

    protected abstract void doHandleMessage(WechatEventMessage message);

}
