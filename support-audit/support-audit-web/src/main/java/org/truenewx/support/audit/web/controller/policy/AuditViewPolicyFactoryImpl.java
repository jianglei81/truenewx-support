package org.truenewx.support.audit.web.controller.policy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核视图方针工厂实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class AuditViewPolicyFactoryImpl<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        implements AuditViewPolicyFactory<U, T, A> {

    private Map<T, AuditViewPolicy<U, T, A>> policies = new HashMap<>();

    @Override
    public void addViewPolicy(final AuditViewPolicy<U, T, A> policy) {
        this.policies.put(policy.getType(), policy);
    }

    @Override
    public AuditViewPolicy<U, T, A> getViewPolicy(final T type) {
        return this.policies.get(type);
    }

}
