package org.truenewx.support.openapi.service;

import org.truenewx.support.openapi.data.model.WechatMessage;

/**
 * 微信开放接口消息侦听器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface WechatMessageListener {

    WechatMessage onReceived(WechatMessage message) throws NoSuchMessageHandlerException;

}
