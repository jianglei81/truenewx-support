package org.truenewx.support.email.send;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.core.util.StringUtil;
import org.truenewx.support.email.EmailMessage;
import org.truenewx.support.email.EmailProvider;
import org.truenewx.support.email.EmailSource;

/**
 * 邮件发送器默认实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EmailSenderImpl implements EmailSender, ContextInitializedBean {
    /**
     * Java邮件发送器
     */
    private JavaMailSender sender;

    private EmailSource source;

    private int interval = 1000;
    /**
     * 邮件实体Map
     */
    private Map<String, EmailProvider> providers = new HashMap<>();
    /**
     * 线程执行器
     */
    private Executor executor;

    public void setSender(final JavaMailSender sender) {
        this.sender = sender;
    }

    public void setSource(final EmailSource source) {
        this.source = source;
    }

    public void setProviders(final Map<String, EmailProvider> providers) {
        if (providers == null) {
            this.providers.clear();
        } else {
            this.providers = providers;
        }
    }

    /**
     *
     * @param executor
     *            线程执行器，未配置时不采用多线程的方式执行
     */
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public EmailProvider getProvider(final String type) {
        return this.providers.get(type);
    }

    @Override
    public void send(final String type, final Iterable<String> addressees,
            final Map<String, Object> params, final Locale locale,
            final EmailSendProgress progress) {
        final EmailProvider provider = getProvider(type);
        if (provider != null) {
            final String title = provider.getTitle(params, locale);
            final String content = provider.getContent(params, locale);
            final List<EmailMessage> messages = new ArrayList<>();
            for (final String addressee : addressees) {
                if (StringUtil.isEmail(addressee)) {
                    messages.add(new EmailMessage(addressee, title, content));
                }
            }
            final EmailSendCommand command = new EmailSendCommand(this.sender, this.source,
                    messages, this.interval, progress);
            if (this.executor == null || progress != null) {
                command.run();
            } else {
                this.executor.execute(command);
            }
        }
    }

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, EmailProvider> providerMap = context.getBeansOfType(EmailProvider.class);
        for (final EmailProvider provider : providerMap.values()) {
            final String type = provider.getType();
            if (type != null) {
                this.providers.put(type, provider);
            }
        }
    }
}
