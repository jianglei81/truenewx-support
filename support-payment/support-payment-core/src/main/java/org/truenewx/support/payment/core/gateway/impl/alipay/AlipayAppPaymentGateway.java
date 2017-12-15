package org.truenewx.support.payment.core.gateway.impl.alipay;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.Program;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.Terminal;
import org.truenewx.support.payment.core.gateway.PaymentExceptionCodes;

/**
 * 支付宝APP支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AlipayAppPaymentGateway extends AlipayPaymentGateway {

    private String appId;
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    public AlipayAppPaymentGateway() {
        setTerminals(new Terminal(null, null, Program.APP));
    }

    public void setAppId(final String appId) {
        this.appId = appId;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
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
            final String sign = RSA.sign(sb.substring(0, sb.lastIndexOf("&")), this.appId,
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
        final Set<Entry<String, String>> entrySet = params.entrySet();
        for (final Entry<String, String> entry : entrySet) {
            final String k = entry.getKey();
            final String v = entry.getValue();
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
