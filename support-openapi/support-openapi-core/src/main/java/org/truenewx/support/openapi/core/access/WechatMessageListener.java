package org.truenewx.support.openapi.core.access;

import org.truenewx.support.openapi.core.model.WechatMessage;

/**
 * 微信开放接口消息侦听器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface WechatMessageListener {

    WechatMessage onReceived(WechatMessage message) throws NoSuchMessageHandlerException;

}