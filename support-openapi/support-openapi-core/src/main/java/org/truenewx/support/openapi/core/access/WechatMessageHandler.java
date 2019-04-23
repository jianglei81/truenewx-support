package org.truenewx.support.openapi.core.access;

import org.springframework.core.Ordered;
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
    WechatMessageType getMessageType();

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(WechatMessageHandler other) {
        return Integer.valueOf(getOrder()).compareTo(Integer.valueOf(other.getOrder()));
    }

}
