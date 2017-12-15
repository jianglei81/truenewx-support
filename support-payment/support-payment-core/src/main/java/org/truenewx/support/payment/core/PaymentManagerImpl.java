package org.truenewx.support.payment.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.model.Terminal;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.payment.core.gateway.PaymentGateway;
import org.truenewx.support.payment.core.gateway.PaymentGatewayAdapter;
import org.truenewx.support.payment.core.gateway.PaymentResult;

/**
 * 支付管理器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class PaymentManagerImpl implements PaymentManager, ContextInitializedBean {

    private Map<String, PaymentGatewayAdapter> gateways = new HashMap<>();
    @Autowired(required = false)
    private PaymentListener listener;

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, PaymentGatewayAdapter> adapters = context
                .getBeansOfType(PaymentGatewayAdapter.class);
        for (final PaymentGatewayAdapter gateway : adapters.values()) {
            final String name = gateway.getName();
            Assert.isNull(this.gateways.put(name, gateway), "More than one gateway named " + name);
        }
    }

    @Override
    public List<PaymentGateway> getGateways(final Terminal terminal) {
        final List<PaymentGateway> gateways = new ArrayList<>();
        for (final PaymentGateway gateway : this.gateways.values()) {
            for (final Terminal t : gateway.getTerminals()) {
                if (t.supports(terminal)) {
                    gateways.add(gateway);
                }
            }
        }
        return gateways;
    }

    @Override
    public PaymentGateway getGateway(final String gatewayName) {
        return this.gateways.get(gatewayName);
    }

    @Override
    public Map<String, String> getRequestParams(final String gatewayName, final Terminal terminal,
            final String orderNo, final BigDecimal amount, final String description,
            final String payerIp) {
        final PaymentGatewayAdapter adapter = this.gateways.get(gatewayName);
        if (adapter != null) {
            return adapter.getRequestParams(terminal, orderNo, amount, description, payerIp);
        }
        return null;
    }

    @Override
    public PaymentResult notifyResult(final String gatewayName, final boolean confirmed,
            final Map<String, String> params) throws HandleableException {
        final PaymentGatewayAdapter adapter = this.gateways.get(gatewayName);
        if (adapter != null) {
            final PaymentResult result = adapter.getResult(confirmed, params);
            if (confirmed && this.listener != null) {
                this.listener.onPaid(adapter.getChannel(), result.getGatewayPaymentNo(),
                        result.getTerminal(), result.getOrderNo());
            }
            return result;
        }
        return null;
    }

    @Override
    public void requestRefund(final String gatewayName, final String gatewayPaymentNo,
            final BigDecimal paymentAmount, final String refundNo, final String refundAmount)
            throws HandleableException {
        final PaymentGatewayAdapter adapter = this.gateways.get(gatewayName);
        if (adapter != null) {
            final String gatewayRefundNo = adapter.requestRefund(gatewayPaymentNo, paymentAmount,
                    refundNo, refundAmount);
            if (this.listener != null) {
                this.listener.onRefunded(refundNo, gatewayRefundNo);
            }
        }
    }

}
