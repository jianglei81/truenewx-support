package org.truenewx.support.payment.core.gateway;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.PaymentDefinition;
import org.truenewx.support.payment.core.PaymentRequestParameter;
import org.truenewx.support.payment.core.PaymentResult;

import java.math.BigDecimal;
import java.util.Map;

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
     * @param definition 支付定义
     * @return 支付请求参数集
     */
    PaymentRequestParameter getRequestParameter(PaymentDefinition definition);

    /**
     * 获取支付结果
     *
     * @param confirmed 是否确认的通知，false-表示结果展示通知
     * @param terminal  支付终端
     * @param params    结果参数集
     * @return 支付结果
     * @throws BusinessException 如果支付结果参数集签名验证失败
     */
    PaymentResult getResult(boolean confirmed, Terminal terminal, Map<String, String> params)
            throws BusinessException;

    /**
     * 发起退款请求
     *
     * @param gatewayPaymentNo 支付网关支付流水号
     * @param paymentAmount    支付金额
     * @param refundNo         退款单编号
     * @param refundAmount     退款金额
     * @return 支付网关退款流水号，返回null说明未成功申请退款
     */
    String requestRefund(String gatewayPaymentNo, BigDecimal paymentAmount, String refundNo,
            String refundAmount);

}
