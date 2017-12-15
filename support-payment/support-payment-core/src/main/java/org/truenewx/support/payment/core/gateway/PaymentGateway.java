package org.truenewx.support.payment.core.gateway;

import org.truenewx.core.model.Named;
import org.truenewx.core.model.Terminal;

/**
 * 支付网关
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface PaymentGateway extends Named {

    boolean isActive();

    PaymentChannel getChannel();

    String getNationCode();

    Terminal[] getTerminals();

    String getLogoUrl();

    boolean isRefundable();

}
