package org.truenewx.support.payment.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 向支付网关发送的请求参数
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PaymentRequestParameter {

    private String url;
    private boolean selfTarget;
    private Map<String, String> params = new HashMap<>();

    public PaymentRequestParameter() {
    }

    public PaymentRequestParameter(Map<String, String> params) {
        if (params != null) {
            this.params = params;
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelfTarget() {
        return this.selfTarget;
    }

    public void setSelfTarget(boolean selfTarget) {
        this.selfTarget = selfTarget;
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}
