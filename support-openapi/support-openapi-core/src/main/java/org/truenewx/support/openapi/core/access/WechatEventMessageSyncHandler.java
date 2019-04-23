package org.truenewx.support.openapi.core.access;

import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.support.openapi.core.model.WechatEventMessage;
import org.truenewx.support.openapi.core.model.WechatEventType;
import org.truenewx.support.openapi.core.model.WechatMessage;
import org.truenewx.support.openapi.core.model.WechatMessageType;

/**
 * 微信开放接口事件消息同步处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatEventMessageSyncHandler implements WechatMessageSyncHandler {

    @Override
    public final WechatMessageType getMessageType() {
        return WechatMessageType.EVENT;
    }

    @Override
    @WriteTransactional
    public final WechatMessage handleMessage(WechatMessage message) {
        WechatEventMessage eventMessage = (WechatEventMessage) message;
        if (eventMessage.getEventType() == getEventType()) {
            return doHandleMessage(eventMessage);
        }
        return null;
    }

    protected abstract WechatEventType getEventType();

    protected abstract WechatMessage doHandleMessage(WechatEventMessage message);

}
