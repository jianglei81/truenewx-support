package org.truenewx.support.audit.service.policy;

import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核方针注册器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            审核申请实体类型
 * @param <T>
 *            申请类型枚举类型
 * @param <A>
 *            审核者类型
 */
public interface AuditPolicyRegistrar<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>> {

    /**
     * 注册审核方针
     *
     * @param policy
     *            审核方针
     */
    void addPolicy(AuditPolicy<U, T, A> policy);

}
