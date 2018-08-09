package org.truenewx.support.payment.core.gateway;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.Terminal;

/**
 * 支付网关适配器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface PaymentGatewayAdapter extends PaymentGateway {

    /**
     * 获取向支付网关发起支付请求所需的参数集
     *
     * @param terminal
     *            终端类型
     * @param orderNo
     *            订单编号
     * @param amount
     *            订单金额
     * @param 币种
     * @param description
     *            订单描述
     * @param payerIp
     *            付款者IP
     *
     * @return 支付请求参数集
     */
    Map<String, String> getRequestParams(Terminal terminal, String orderNo, BigDecimal amount,
            Currency currency, String description, String payerIp);

    /**
     * 获取支付结果
     *
     * @param confirmed
     *            是否确认的通知，false-表示结果展示通知
     * @param params
     *            结果参数集
     * @return 支付结果
     * @throws BusinessException
     *             如果支付结果参数集签名验证失败
     */
    PaymentResult getResult(boolean confirmed, Map<String, String> params) throws BusinessException;

    /**
     * 发起退款请求
     *
     * @param gatewayPaymentNo
     *            支付网关支付流水号
     * @param paymentAmount
     *            支付金额
     * @param refundNo
     *            退款单编号
     * @param refundAmount
     *            退款金额
     * @return 支付网关退款流水号，返回null说明未成功申请退款
     */
    String requestRefund(String gatewayPaymentNo, BigDecimal paymentAmount, String refundNo,
            String refundAmount);

}
