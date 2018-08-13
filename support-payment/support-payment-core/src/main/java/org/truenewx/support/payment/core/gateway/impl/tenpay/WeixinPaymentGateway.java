package org.truenewx.support.payment.core.gateway.impl.tenpay;

import org.truenewx.support.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：支付宝
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
