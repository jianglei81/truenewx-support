package org.truenewx.support.payment.core.gateway.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.gateway.PaymentChannel;
import org.truenewx.support.payment.core.gateway.PaymentResult;

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
    public Map<String, String> getRequestParams(final Terminal terminal, final String orderNo,
            final BigDecimal amount, final String description, final String payerIp) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PaymentResult getResult(final boolean confirmed, final Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

}
