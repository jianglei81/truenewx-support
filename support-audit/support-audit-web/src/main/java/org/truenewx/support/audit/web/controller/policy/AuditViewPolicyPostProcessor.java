package org.truenewx.support.audit.web.controller.policy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核视图方针提交处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class AuditViewPolicyPostProcessor<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        implements BeanPostProcessor {

    private AuditViewPolicyFactory<U, T, A> policyFactory;

    @Autowired(required = false)
    public void setPolicyFactory(final AuditViewPolicyFactory<U, T, A> policyFactory) {
        this.policyFactory = policyFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
            throws BeansException {
        if (bean instanceof AuditViewPolicy && this.policyFactory != null) {
            this.policyFactory.addViewPolicy((AuditViewPolicy<U, T, A>) bean);
        }
        return bean;
    }

}
