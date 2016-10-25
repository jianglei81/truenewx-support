package org.truenewx.support.audit.service.policy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核方针Bean提交处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class AuditPolicyPostProcessor<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        implements BeanPostProcessor {

    private AuditPolicyRegistrar<U, T, A> policyRegistrar;

    @Autowired(required = false)
    public void setPolicyRegistrar(final AuditPolicyRegistrar<U, T, A> policyRegistrar) {
        this.policyRegistrar = policyRegistrar;
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
        if (bean instanceof AuditPolicy && this.policyRegistrar != null) {
            this.policyRegistrar.addPolicy((AuditPolicy<U, T, A>) bean);
        }
        return bean;
    }

}
