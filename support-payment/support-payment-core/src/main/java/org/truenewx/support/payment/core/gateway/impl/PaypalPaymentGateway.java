package org.truenewx.support.payment.core.gateway.impl;

import java.util.Map;

import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.PaymentDefinition;
import org.truenewx.support.payment.core.PaymentRequestParameter;
import org.truenewx.support.payment.core.PaymentResult;
import org.truenewx.support.payment.core.gateway.PaymentChannel;

/**
 * 支付网关：PayPal 贝宝
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PaypalPaymentGateway extends AbstractPaymentGateway {

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.PAYPAL;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PaymentResult getResult(boolean confirmed, Terminal terminal, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

}
