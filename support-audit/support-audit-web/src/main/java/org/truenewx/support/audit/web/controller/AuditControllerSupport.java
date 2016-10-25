package org.truenewx.support.audit.web.controller;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.truenewx.core.Strings;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;
import org.truenewx.support.audit.web.controller.policy.AuditViewPolicy;
import org.truenewx.support.audit.web.controller.policy.AuditViewPolicyFactory;

/**
 * 审核控制器支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditControllerSupport<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        implements MessageSourceAware {
    /**
     * 审核清单中内容字段的消息前缀
     */
    private static final String LIST_CONENT_FIELD_PREFIX = "constant.audit.content.field.";

    @Autowired
    private AuditApplymentUnityService<U, T, A> service;
    @Autowired
    private AuditViewPolicyFactory<U, T, A> viewPolicyFactory;
    private MessageSource messageSource;

    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @SuppressWarnings("unchecked")
    protected <S extends AuditApplymentUnityService<U, T, A>> S getService() {
        return (S) this.service;
    }

    protected AuditViewPolicy<U, T, A> getViewPolicy(final T type) {
        return this.viewPolicyFactory.getViewPolicy(type);
    }

    protected Map<String, String> getListContentFields(final T type, final Locale locale) {
        final AuditViewPolicy<U, T, A> policy = getViewPolicy(type);
        if (policy != null) {
            final String[] fields = policy.getListContentFields();
            if (fields != null && fields.length > 0) {
                final Map<String, String> result = new LinkedHashMap<>(); // 保持顺序
                for (final String field : fields) {
                    // 格式：前缀.申请类型.字段名
                    final StringBuffer code = new StringBuffer(LIST_CONENT_FIELD_PREFIX)
                            .append(type.name().toLowerCase()).append(Strings.DOT).append(field);
                    final String caption = this.messageSource.getMessage(code.toString(), null,
                            Strings.EMPTY, locale);
                    result.put(field, caption);
                }
                return result;
            }
        }
        return null;
    }
}
