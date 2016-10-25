package org.truenewx.support.audit.service.state;

import org.truenewx.support.audit.data.enums.AuditStatus;
import org.truenewx.support.audit.data.enums.AuditTransition;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 审核拒绝动作
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 * @param <T>
 * @param <A>
 */
public class AuditRejectAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AuditorAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.REJECT;
    }

    @Override
    public AuditStatus[] getStates() {
        return new AuditStatus[] { AuditStatus.PENDING, AuditStatus.PASSED_1,
                AuditStatus.REJECTED_2 }; // 未审核的，一审通过的，二审拒绝的
    }

    @Override
    public AuditStatus getNextState(final AuditStatus state, final Object context) {
        @SuppressWarnings("unchecked")
        final T type = (T) context;
        final AuditPolicy<U, T, A> policy = getPolicy(type);
        if (policy != null) {
            final byte levels = policy.getLevels();
            switch (state) {
            case PENDING:
                return AuditStatus.REJECTED_1;
            case PASSED_1:
                return levels == 2 ? AuditStatus.REJECTED_2 : null;
            case REJECTED_2:
                return levels == 2 ? AuditStatus.REJECTED_1 : null;
            default:
                return null;
            }
        }
        return null;
    }

}
