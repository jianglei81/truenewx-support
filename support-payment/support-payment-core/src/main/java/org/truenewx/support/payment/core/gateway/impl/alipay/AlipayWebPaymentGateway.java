package org.truenewx.support.payment.core.gateway.impl.alipay;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.enums.Program;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.Terminal;
import org.truenewx.core.util.EncryptUtil;
import org.truenewx.support.payment.core.gateway.PaymentExceptionCodes;

/**
 * 支付宝网页支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AlipayWebPaymentGateway extends AlipayPaymentGateway {

    private String privateKey;

    public AlipayWebPaymentGateway() {
        setTerminals(new Terminal(null, null, Program.WEB));
    }

    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
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
                sb.append(k + "=" + v + "&");
            }
        }
        final String sign = EncryptUtil
                .encryptByMd5(sb.substring(0, sb.lastIndexOf("&")) + this.privateKey);
        params.put("sign_type", "MD5"); // 签名类型,默认：MD5
        params.put("sign", sign);

        // this.setDebugInfo(sb.toString() + " => sign:" + sign);
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
        final String sign = EncryptUtil
                .encryptByMd5(sb.substring(0, sb.lastIndexOf("&")) + this.privateKey);
        if (!sign.equals(params.get("sign").toLowerCase())) {
            throw new BusinessException(PaymentExceptionCodes.SIGN_FAIL);
        }
    }

}
