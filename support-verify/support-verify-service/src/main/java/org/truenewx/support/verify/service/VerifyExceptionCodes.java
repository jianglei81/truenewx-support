package org.truenewx.support.verify.service;

/**
 * 校验异常代码集
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class VerifyExceptionCodes {

    private VerifyExceptionCodes() {
    }

    /**
     * 不支持的验证类型
     */
    public static final String UNSUPPORTED_TYPE = "error.verify.unsupported_type";

    /**
     * 验证码无效，错误或已过期
     */
    public static final String INVALID_CODE = "error.verify.invalid_code";

    /**
     * 验证码错误
     */
    public static final String WRONG_CODE = "error.verify.wrong_code";

    /**
     * 验证码已过期
     */
    public static final String OVERDUE_CODE = "error.verify.overdue_code";

    /**
     * 已验证
     */
    public static final String VERIFIED = "error.verify.verified";

}
