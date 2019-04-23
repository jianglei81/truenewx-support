package org.truenewx.support.openapi.core.access;

import org.truenewx.support.openapi.core.model.WechatMessage;

/**
 * 微信开放接口消息异步处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface WechatMessageAsynHandler extends WechatMessageHandler {

    /**
     * 当接收到指定类型的消息时触发的同步处理方法，请求不会等待本方法执行完毕
     *
     * @param message 消息
     */
    void handleMessage(WechatMessage message);

}
