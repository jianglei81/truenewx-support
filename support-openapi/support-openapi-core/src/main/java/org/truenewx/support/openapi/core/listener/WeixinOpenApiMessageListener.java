package org.truenewx.support.openapi.core.listener;

import org.truenewx.support.openapi.core.model.WeixinOpenApiMessage;

/**
 * 微信开放接口消息侦听器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface WeixinOpenApiMessageListener {

    void onReceived(WeixinOpenApiMessage message);

}