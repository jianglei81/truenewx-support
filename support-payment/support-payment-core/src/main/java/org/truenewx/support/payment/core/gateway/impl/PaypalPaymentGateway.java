package org.truenewx.support.payment.core.gateway.impl;

import java.math.BigDecimal;
import java.util.Currency;
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
    public Map<String, String> getRequestParams(Terminal terminal, String orderNo,
            BigDecimal amount, Currency currency, String description, String payerIp) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PaymentResult getResult(boolean confirmed, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

}
