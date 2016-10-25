package org.truenewx.support.sms.send;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.truenewx.core.Strings;
import org.truenewx.support.sms.SmsProvider;

/**
 * 短信发送器实现
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class SmsSenderImpl implements SmsSender, InitializingBean {
    private Map<String, SmsContentSender> contentSenders = new HashMap<>();
    private Map<String, SmsProvider> providers = new HashMap<>();
    private boolean disabled;

    public void setContentSenders(final Map<String, SmsContentSender> contentSenders) {
        this.contentSenders = contentSenders;
    }

    public void setProviders(final List<SmsProvider> providers) {
        for (final SmsProvider provider : providers) {
            this.providers.put(provider.getType(), provider);
        }
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.disabled) {
            this.providers.clear();
        }
    }

    private SmsContentSender getContentSender(final String type) {
        SmsContentSender contentSender = this.contentSenders.get(type);
        if (contentSender == null) {
            contentSender = this.contentSenders.get(Strings.ASTERISK); // 默认内容发送器
        }
        return contentSender;
    }

    @Override
    public SmsSendResult send(final String type, final Map<String, Object> params,
            final Locale locale, final String... mobilePhones) {
        final SmsProvider provider = this.providers.get(type);
        if (provider != null) {
            final String content = provider.getContent(params, locale);
            if (content != null) {
                final SmsContentSender contentSender = getContentSender(type);
                if (contentSender != null) {
                    return contentSender.send(content, provider.getMaxCount(), mobilePhones);
                }
            }
        }
        return null;
    }

    @Override
    public void send(final String type, final Map<String, Object> params, final Locale locale,
            final String[] mobilePhones, final SmsSendCallback callback) {
        final SmsProvider provider = this.providers.get(type);
        if (provider != null) {
            final String content = provider.getContent(params, locale);
            if (content != null) {
                final SmsContentSender contentSender = getContentSender(type);
                if (contentSender != null) {
                    contentSender.send(content, provider.getMaxCount(), mobilePhones, callback);
                }
            }
        }
    }
}
