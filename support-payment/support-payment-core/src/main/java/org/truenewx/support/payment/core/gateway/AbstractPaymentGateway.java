package org.truenewx.support.payment.core.gateway;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truenewx.core.model.Terminal;
import org.truenewx.core.region.RegionNationCodes;

/**
 * 抽象的支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractPaymentGateway implements PaymentGatewayAdapter {
    private String name;
    private boolean active;
    private String nationCode = RegionNationCodes.CHINA;
    private Terminal[] terminals;
    private String logoUrl;
    private boolean refundable;
    private String resultConfirmUrl;
    private String resultShowUrl;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String getNationCode() {
        return this.nationCode;
    }

    public void setNationCode(final String nationCode) {
        this.nationCode = nationCode;
    }

    @Override
    public Terminal[] getTerminals() {
        return this.terminals;
    }

    public void setTerminals(final Terminal... terminals) {
        this.terminals = terminals;
    }

    @Override
    public String getLogoUrl() {
        return this.logoUrl;
    }

    public void setLogoUrl(final String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public boolean isRefundable() {
        return this.refundable;
    }

    public void setRefundable(final boolean refundable) {
        this.refundable = refundable;
    }

    protected String getResultConfirmUrl() {
        return replaceName(this.resultConfirmUrl);
    }

    private String replaceName(String url) {
        if (url != null && url.contains("{name}")) {
            url = url.replaceAll("\\{name\\}", this.name);
        }
        return url;
    }

    public void setResultConfirmUrl(final String resultConfirmUrl) {
        this.resultConfirmUrl = resultConfirmUrl;
    }

    protected String getResultShowUrl() {
        return replaceName(this.resultShowUrl);
    }

    public void setResultShowUrl(final String resultShowUrl) {
        this.resultShowUrl = resultShowUrl;
    }

    @Override
    public String requestRefund(final String gatewayPaymentNo, final BigDecimal paymentAmount,
            final String refundNo, final String refundAmount) {
        // 默认不支持退款
        return null;
    }
}
