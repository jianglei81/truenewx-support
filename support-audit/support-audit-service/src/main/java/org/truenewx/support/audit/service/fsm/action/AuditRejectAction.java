package org.truenewx.support.audit.service.fsm.action;

import org.springframework.stereotype.Service;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.model.AuditorIdentity;
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
@Service
public class AuditRejectAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AuditorAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.REJECT;
    }

    @Override
    public AuditState getNextState(final AuditUserIdentity userIdentity, final AuditState state,
            final Object context) {
        final T type = getApplymentTypeByContext(context);
        final AuditPolicy<U, T, A> policy = loadPolicy(type);
        final byte levels = policy.getLevels();
        if (userIdentity instanceof AuditorIdentity) {
            switch (state) {
            case PENDING:
                return AuditState.REJECTED_1;
            case PASSED_1:
                return levels == 2 ? AuditState.REJECTED_2 : null;
            case REJECTED_2:
                return levels == 2 ? AuditState.REJECTED_1 : null;
            default:
                return null;
            }
        }
        return null;
    }

}
