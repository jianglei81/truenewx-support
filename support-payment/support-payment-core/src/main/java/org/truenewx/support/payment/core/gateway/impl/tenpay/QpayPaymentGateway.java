package org.truenewx.support.payment.core.gateway.impl.tenpay;

import org.truenewx.support.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：QQ钱包
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class QpayPaymentGateway extends TenpayPaymentGateway {

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.QPAY;
    }

}
