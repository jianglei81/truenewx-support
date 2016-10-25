package org.truenewx.support.sms;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.truenewx.core.parser.SimpleElTemplateParser;
import org.truenewx.core.parser.TemplateParser;

/**
 * 基于模版的短信提供者
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class TemplateSmsProvider extends AbstractSmsProvider implements MessageSourceAware {
    private String code;
    private MessageSource messageSource;
    private TemplateParser parser = new SimpleElTemplateParser();

    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setParser(final TemplateParser parser) {
        this.parser = parser;
    }

    @Override
    public String getContent(final Map<String, Object> params, final Locale locale) {
        final String templateContent = this.messageSource.getMessage(this.code, null, locale);
        return this.parser.parse(templateContent, params, locale);
    }
}
