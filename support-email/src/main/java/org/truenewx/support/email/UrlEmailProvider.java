package org.truenewx.support.email;

import java.util.Locale;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.core.parser.TemplateParser;
import org.truenewx.core.util.NetUtil;

/**
 * 基于URL的邮件提供者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UrlEmailProvider extends AbstractEmailProvider {

    private String url;
    /**
     * 模板解析器
     */
    private TemplateParser parser;

    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * 设置模板解析器
     *
     * @param parser
     *            模板解析器
     */
    public void setParser(final TemplateParser parser) {
        this.parser = parser;
    }

    @Override
    public String getTitle(final Map<String, Object> params, final Locale locale) {
        if (this.parser != null) {
            try {
                return this.parser.parse(this.title, params, locale);
            } catch (final Exception e) {
                this.logger.error(e.getMessage(), e);
                return Strings.EMPTY;
            }
        } else {
            return this.title;
        }
    }

    @Override
    public String getContent(final Map<String, Object> params, final Locale locale) {
        try {
            return NetUtil.requestByPost(this.url, params, null);
        } catch (final Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

}
