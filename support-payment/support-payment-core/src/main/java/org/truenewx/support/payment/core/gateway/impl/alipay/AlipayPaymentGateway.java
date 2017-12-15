package org.truenewx.support.payment.core.gateway.impl.alipay;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.truenewx.core.Strings;
import org.truenewx.core.enums.Program;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.Terminal;
import org.truenewx.core.util.MathUtil;
import org.truenewx.support.payment.core.gateway.PaymentChannel;
import org.truenewx.support.payment.core.gateway.PaymentResult;
import org.truenewx.support.payment.core.gateway.impl.AbstractPaymentGateway;

/**
 * 支付网关：支付宝
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AlipayPaymentGateway extends AbstractPaymentGateway {

    private String partner;

    public void setPartner(final String partner) {
        this.partner = partner;
    }

    @Override
    public PaymentChannel getChannel() {
        return PaymentChannel.ALIPAY;
    }

    @Override
    public Map<String, String> getRequestParams(final Terminal terminal, final String orderNo,
            final BigDecimal amount, final String description, final String payerIp) {
        final SortedMap<String, String> params = new TreeMap<>();
        params.put("service", "create_direct_pay_by_user");
        params.put("partner", this.partner);
        params.put("seller_id", this.partner);
        params.put("_input_charset", Strings.ENCODING_UTF8.toLowerCase());
        params.put("payment_type", "1");
        params.put("notify_url", getResultConfirmUrl());
        if (terminal.getProgram() == Program.WEB) { // 网页才需要提供结果展示页URL
            params.put("return_url", getResultShowUrl());
        }
        params.put("out_trade_no", orderNo);
        params.put("total_fee", amount.toString());
        // final String body =
        // this.messageSource.getMessage("info.payment.body",
        // new String[] { description }, Locale.getDefault());
        params.put("subject", description);
        params.put("body", description);

        sign(params);
        return params;
    }

    protected abstract void sign(final SortedMap<String, String> params);

    @Override
    public PaymentResult getResult(final boolean confirmed, final Map<String, String> params)
            throws BusinessException {
        validateSign(params);
        final String paymentStatus = params.get("trade_status"); // 支付状态
        if ("TRADE_SUCCESS".equals(paymentStatus)) { // 支付结果不等于0，支付失败
            final String gatewayPaymentNo = params.get("trade_no"); // 支付交易号
            final String fee = params.get("total_fee"); // 金额，以分为单位
            final BigDecimal amount = new BigDecimal(fee).divide(MathUtil.HUNDRED); // 转换为以元为单位的金额
            final Terminal terminal = null; // TODO 终端类型
            final String orderNo = params.get("out_trade_no"); // 商户订单号
            return new PaymentResult(gatewayPaymentNo, amount, terminal, orderNo, "success");
        }
        return null; // 状态不为成功，则一律返回null
    }

    protected abstract void validateSign(final Map<String, String> params) throws BusinessException;

}
