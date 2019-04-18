package org.truenewx.support.openapi.core.access;

import org.springframework.core.Ordered;
import org.truenewx.support.openapi.core.model.WechatMessage;
import org.truenewx.support.openapi.core.model.WechatMessageType;

/**
 * 微信开放接口消息处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface WechatMessageHandler extends Ordered, Comparable<WechatMessageHandler> {

    /**
     *
     * @return 侦听的消息类型集合
     */
    WechatMessageType[] getMessageTypes();

    /**
     * 当接收到指定类型的消息时触发的处理方法
     *
     * @param message 消息
     * @return 处理完消息后返回给微信服务端的消息，返回null表示没有返回消息
     */
    WechatMessage handleMessage(WechatMessage message);

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(WechatMessageHandler other) {
        return Integer.valueOf(getOrder()).compareTo(Integer.valueOf(other.getOrder()));
    }

}
