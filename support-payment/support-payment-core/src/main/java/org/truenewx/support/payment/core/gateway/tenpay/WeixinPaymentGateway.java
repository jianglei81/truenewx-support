package org.truenewx.support.payment.core.gateway.tenpay;

import org.truenewx.support.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：微信
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class WeixinPaymentGateway extends TenpayPaymentGateway {

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.WEIXIN;
    }

}
