package org.truenewx.support.payment.core.gateway;

import org.truenewx.core.exception.BusinessException;

/**
 * 具有响应内容的业务异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RespondBusinessException extends BusinessException {

    private static final long serialVersionUID = 8762277251494217080L;

    private String response;

    public RespondBusinessException(final String response, final String code,
            final Object... args) {
        super(code, args);
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

}
