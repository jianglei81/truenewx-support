package org.truenewx.support.email;

import java.util.Locale;
import java.util.Map;

/**
 * 邮件提供者
 *
 * @author jianglei
 * @since JDK 1.7
 */
public interface EmailProvider {

    /**
     * 获取邮件类型
     *
     * @return 邮件类型
     */
    public String getType();

    /**
     * 用指定参数集解析获取邮件主题
     *
     * @param params
     *            解析参数集
     * @param locale
     *            区域
     *
     * @return 邮件主题
     */
    public String getTitle(Map<String, Object> params, Locale locale);

    /**
     * 用指定参数集解析获取邮件内容
     *
     * @param params
     *            解析参数集
     * @param locale
     *            区域
     *
     * @return 邮件内容
     */
    public String getContent(Map<String, Object> params, Locale locale);

}
