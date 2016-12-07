package org.truenewx.support.verify.service.policy;

import java.util.Locale;
import java.util.Map;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.verify.data.model.VerifyUnity;

/**
 * 验证方针
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public interface VerifyPolicy<E extends VerifyUnity<T>, T extends Enum<T>> {
    /**
     * 获取当前方针支持的验证类型。一个方针支持且仅能支持一个验证类型
     *
     * @return 验证类型
     */
    T getVerifyType();

    /**
     * 校验内容
     *
     * @param content
     *            内容
     * @throws HandleableException
     *             如果校验失败
     */
    void validate(Map<String, Object> content) throws HandleableException;

    /**
     * 生成验证码。可在本方法中校验验证内容
     *
     * @param content
     *            验证内容
     *
     * @return 新的验证码，如果返回null，将无法进行后续操作
     */
    String generateCode(Map<String, Object> content);

    /**
     * 获取验证码过期时间间隔，单位：毫秒
     *
     * @param content
     *            验证内容
     * @return 验证码过期时间间隔
     */
    long getExpiredInterval(Map<String, Object> content);

    /**
     * 发送验证码
     *
     * @param code
     *            验证码
     * @param content
     *            验证内容
     * @param locale
     *            区域
     */
    void send(String code, Map<String, Object> content, Locale locale);

    /**
     * 指定内容被验证通过完毕后的处理
     *
     * @param entity
     *            验证实体
     * @param context
     *            验证确认时的上下文
     * @return 是否需要删除验证记录
     * @throws HandleableException
     *             处理过程中如果出现错误
     */
    boolean onVerified(E entity, Object context) throws HandleableException;
}
