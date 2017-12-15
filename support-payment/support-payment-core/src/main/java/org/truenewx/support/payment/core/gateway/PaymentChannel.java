package org.truenewx.support.payment.core.gateway;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 支付渠道
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum PaymentChannel {
    @Caption("支付宝")
    @EnumValue("alipay")
    ALIPAY,

    @Caption("财付通")
    @EnumValue("tenpay")
    TENPAY,

    @Caption("微信支付")
    @EnumValue("weixin")
    WEIXIN,

    @Caption("QQ钱包")
    @EnumValue("qpay")
    QPAY,

    @Caption("PayPal")
    @EnumValue("paypal")
    PAYPAL,

    @Caption("Apple Pay")
    @EnumValue("apple")
    APPLE,

    @Caption("Google支付")
    @EnumValue("google")
    GOOGLE;
}
