package org.truenewx.support.payment.core.gateway;

/**
 * 支付模块异常错误码常量类
 *
 * @author ouyongfa
 * @version 1.0.0 2013年12月12日
 * @since JDK 1.7
 */
public class PaymentExceptionCodes {
    private PaymentExceptionCodes() {
    }

    /**
     * 支付失败
     */
    public static final String PAYMENT_FAIL = "error.payment.fail";

    /**
     * 不是即时到账时返回失败结果
     */
    public static final String NOT_INSTANT_TO_ACCOUNT = "error.payment.not_instant_to_account";

    /**
     * 签名验证失败
     */
    public static final String SIGN_FAIL = "error.payment.sign_fail";

    /**
     * 有可能因为网络原因，请求已经处理，但未收到应答
     */
    public static final String CONNECTION_FAIL = "error.payment.connection_fail";

    /**
     * 不确定的退款
     */
    public static final String UNSURE_REFUND = "error.payment.refund.unsure";

    /**
     * 支付网关退款错误
     */
    public static final String GATEWAY_REFUND_ERROR = "error.payment.refund.gateway";

    /**
     * 无效的退款收款账号
     */
    public static final String INVALID_REFUND_RECEIPIENT = "error.payment.refund.invalid_receipient";

}
