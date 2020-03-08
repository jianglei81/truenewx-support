package org.truenewx.support.payment.core;

import org.truenewx.core.model.Terminal;

import java.math.BigDecimal;

/**
 * 支付结果
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PaymentResult {
    /**
     * 支付网关支付流水号
     */
    private String gatewayPaymentNo;
    /**
     * 支付金额
     */
    private BigDecimal amount;
    /**
     * 支付终端类型
     */
    private Terminal terminal;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 返回给支付网关的响应内容
     */
    private String response;

    public PaymentResult(final String gatewayPaymentNo, final BigDecimal amount,
            final Terminal terminal, final String orderNo, final String response) {
        this.gatewayPaymentNo = gatewayPaymentNo;
        this.amount = amount;
        this.terminal = terminal;
        this.orderNo = orderNo;
        this.response = response;
    }

    public String getGatewayPaymentNo() {
        return this.gatewayPaymentNo;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getResponse() {
        return this.response;
    }

}
