package org.truenewx.support.payment.core.gateway.alipay;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.Program;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.Terminal;
import org.truenewx.core.region.RegionNationCodes;
import org.truenewx.support.payment.core.PaymentDefinition;
import org.truenewx.support.payment.core.PaymentRequestParameter;
import org.truenewx.support.payment.core.gateway.PaymentExceptionCodes;

/**
 * 支付宝APP支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AlipayAppPaymentGateway extends AlipayPaymentGateway {

    private String privateKey;
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    public AlipayAppPaymentGateway() {
        setTerminals(new Terminal(null, null, Program.APP));
    }

    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public PaymentRequestParameter getRequestParameter(PaymentDefinition definition) {
        SortedMap<String, String> params = new TreeMap<>();
        params.put("_input_charset", Strings.ENCODING_UTF8.toLowerCase());
        params.put("appenv", definition.getTerminal().getOs().toString().toLowerCase());
        params.put("body", definition.getDescription());
        params.put("currency", definition.getCurrency().toString());
        params.put("notify_url", this.getResultConfirmUrl());
        if (RegionNationCodes.HONG_KONG.equals(getNationCode())) {
            params.put("forex_biz", "FP");// 官方未有表明,客服确认用于标识境外移动端
            params.put("payment_inst", "ALIPAYHK");// 表明需要打开海外支付宝版本
            params.put("product_code", "NEW_WAP_OVERSEAS_SELLER");// 用来区分新境外收单还是旧版本的境外收单
        }
        params.put("out_trade_no", definition.getOrderNo());
        params.put("partner", this.partner);
        params.put("payment_type", "1");
        params.put("seller_id", this.partner);
        params.put("service", "mobile.securitypay.pay");
        params.put("subject", definition.getDescription());
        params.put("total_fee", definition.getAmount().toString());
        sign(params);
        return new PaymentRequestParameter(params);
    }

    @Override
    protected void sign(final SortedMap<String, String> params) {
        final StringBuffer sb = new StringBuffer();
        final Set<Entry<String, String>> entrySet = params.entrySet();
        for (final Entry<String, String> entry : entrySet) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k)
                    && !"sign_type".equals(k)) {
                sb.append(k + "=\"" + v + "\"&");
            }
        }
        try {
            final String sign = RSA.sign(sb.substring(0, sb.lastIndexOf("&")), this.privateKey,
                    Strings.ENCODING_UTF8);
            params.put("sign", sign);
            params.put("sign_type", "RSA");
            params.put("request_content", sb.substring(0, sb.lastIndexOf("&")) + "&sign=\""
                    + URLEncoder.encode(sign, Strings.ENCODING_UTF8) + "\"" + "&sign_type=\"RSA\"");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void validateSign(final Map<String, String> params) throws BusinessException {
        final StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            String v = params.get(k);
            if (StringUtils.isNotBlank(v) && !"sign".equals(k) && !"key".equals(k)
                    && !"sign_type".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }

        if (!RSA.verify(sb.substring(0, sb.lastIndexOf("&")), params.get("sign"), this.publicKey,
                Strings.ENCODING_UTF8)) {
            throw new BusinessException(PaymentExceptionCodes.SIGN_FAIL);
        }
    }
}
