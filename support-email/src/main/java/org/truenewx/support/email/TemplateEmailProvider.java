package org.truenewx.support.email;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.truenewx.core.Strings;
import org.truenewx.core.parser.TemplateParser;

/**
 * 基于模板的邮件提供者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TemplateEmailProvider extends AbstractEmailProvider {
    /**
     * 邮件模板资源
     */
    private Resource resource;

    /**
     * 模板解析器
     */
    private TemplateParser parser;

    public void setResource(final Resource resource) {
        this.resource = resource;
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
        try {
            return this.parser.parse(this.title, params, locale);
        } catch (final Exception e) {
            e.printStackTrace();
            return Strings.EMPTY;
        }
    }

    @Override
    public String getContent(final Map<String, Object> params, final Locale locale) {
        try {
            return this.parser.parse(this.resource.getFile(), params, locale);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
