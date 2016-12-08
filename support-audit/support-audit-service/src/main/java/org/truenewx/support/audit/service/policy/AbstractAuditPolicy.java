package org.truenewx.support.audit.service.policy;

import java.util.Map;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.service.ServiceSupport;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;

/**
 * 抽象的审核方针
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
public abstract class AbstractAuditPolicy<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ServiceSupport implements AuditPolicy<U, T, A> {

    @Override
    public void appendParams(final AuditApplymentUnityQueryParameter parameter,
            final Map<String, String[]> params) {
    }

    @Override
    public void onPassed(final U applyment, final Object addition) throws HandleableException {
    }

    @Override
    public void onRejected(final U applyment) throws HandleableException {
    }
}
