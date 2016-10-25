package org.truenewx.support.audit.web.controller.policy;

import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核视图方针工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuditViewPolicyFactory<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>> {

    void addViewPolicy(AuditViewPolicy<U, T, A> policy);

    AuditViewPolicy<U, T, A> getViewPolicy(T type);

}
