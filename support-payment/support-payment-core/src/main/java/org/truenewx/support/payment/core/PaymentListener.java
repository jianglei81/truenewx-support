package org.truenewx.support.payment.core;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.gateway.PaymentChannel;

/**
 * 支付侦听器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface PaymentListener {

    /**
     * 在完成支付后被调用
     * @param channel
     *            支付渠道
     * @param gatewayPaymentNo
     *            支付网关支付流水号
     * @param terminal TODO
     * @param orderNo
     *            订单编号
     */
    void onPaid(PaymentChannel channel, String gatewayPaymentNo, Terminal terminal, String orderNo)
            throws HandleableException;

    /**
     * 在完成退款请求后被调用
     *
     * @param refundNo
     *            退款单编号
     * @param gatewayRefundNo
     *            支付网关退款流水号
     */
    void onRefunded(String refundNo, String gatewayRefundNo) throws HandleableException;

}
