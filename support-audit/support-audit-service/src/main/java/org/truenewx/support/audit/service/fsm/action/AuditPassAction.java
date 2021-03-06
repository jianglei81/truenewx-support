package org.truenewx.support.audit.service.fsm.action;

import org.springframework.stereotype.Service;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 审核通过动作
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
@Service
public class AuditPassAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AuditorAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.PASS;
    }

    @Override
    public AuditState[] getBeginStates() {
        return new AuditState[] { AuditState.PENDING, AuditState.PASSED_1, AuditState.REJECTED_2 };
    }

    @Override
    public AuditState getEndState(final AuditState beginState, final Object condition) {
        @SuppressWarnings("unchecked")
        final T type = (T) condition;
        final AuditPolicy<U, T, A> policy = loadPolicy(type);
        final byte levels = policy.getLevels();
        switch (beginState) {
        case PENDING:
            return levels == 1 ? AuditState.PASSED_LAST : AuditState.PASSED_1;
        case PASSED_1:
            return levels == 2 ? AuditState.PASSED_LAST : null;
        case REJECTED_2:
            return levels == 2 ? AuditState.PASSED_1 : null;
        default:
            return null;
        }
    }

}
