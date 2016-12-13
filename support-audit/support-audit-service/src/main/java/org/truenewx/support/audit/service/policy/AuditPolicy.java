package org.truenewx.support.audit.service.policy;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核方针
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
public interface AuditPolicy<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>> {

    T getType();

    /**
     * 获取审核级别数，暂时仅支持1和2
     *
     * @return 审核级别数
     */
    byte getLevels();

    /**
     * 终审通过后调用
     *
     * @param applyment
     *            申请实体
     * @param addition
     *            终审通过时的附加数据
     * @throws HandleableException
     */
    void onPassed(U applyment, Object addition) throws HandleableException;

    /**
     * 审核拒绝后调用
     *
     * @param applyment
     *            申请实体
     * @throws HandleableException
     */
    void onRejected(U applyment) throws HandleableException;
}
