package org.truenewx.support.verify.service;

import java.util.Locale;
import java.util.Map;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.service.Service;
import org.truenewx.verify.data.model.VerifyEntity;

/**
 * 验证器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <E>
 *            验证实体类型
 * @param <T>
 *            验证类型枚举类型
 */
public interface Verifier<E extends VerifyEntity<T>, T extends Enum<T>> extends Service {

    /**
     * 首次发送验证信息
     *
     * @param type
     *            类型
     * @param content
     *            内容
     * @param locale
     *            区域
     * @return 验证信息实体对象id
     * @throws HandleableException
     *             如果发送过程出现错误
     */
    Long send(T type, Map<String, Object> content, Locale locale) throws HandleableException;

    /**
     * 重新发送验证信息
     *
     * @param id
     *            验证信息id
     * @param locale
     *            区域
     * @throws HandleableException
     *             如果发送过程出现错误
     */
    void resend(long id, Locale locale) throws HandleableException;

    /**
     * 判断指定验证码在指定验证对象中是否有效
     *
     * @param id
     *            验证id
     * @param code
     *            验证码
     * @return 是否有效
     */
    boolean isValid(long id, String code);

    /**
     * 判断指定验证码是否有效
     *
     * @param code
     *            验证码
     * @return 是否有效
     */
    boolean isValid(String code);

    /**
     * 验证确认
     *
     * @param id
     *            验证信息id
     * @param code
     *            验证码
     * @param context
     *            验证确认时的上下文
     * @return 验证消息对象
     * @throws HandleableException
     *             如果验证确认过程中出现错误
     */
    E verify(long id, String code, Object context) throws HandleableException;

    /**
     * 验证确认
     *
     * @param code
     *            验证码
     * @param context
     *            验证确认时的上下文
     *
     * @return 验证消息对象
     * @throws HandleableException
     *             如果验证确认过程中出现错误
     */
    E verify(String code, Object context) throws HandleableException;

    /**
     * 清理过期的验证信息
     */
    void clean();
}
