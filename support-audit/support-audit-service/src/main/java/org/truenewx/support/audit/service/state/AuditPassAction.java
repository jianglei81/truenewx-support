package org.truenewx.support.audit.service.state;

import org.springframework.stereotype.Service;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
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
    public AuditStatus[] getStates() {
        return new AuditStatus[] { AuditStatus.PENDING, AuditStatus.PASSED_1,
                AuditStatus.REJECTED_2 }; // 未审核的，一审通过的，二审拒绝的
    }

    @Override
    public AuditStatus getNextState(final AuditStatus state, final Object context) {
        @SuppressWarnings("unchecked")
        final T type = (T) context;
        final AuditPolicy<U, T, A> policy = loadPolicy(type);
        final byte levels = policy.getLevels();
        switch (state) {
        case PENDING:
            return levels == 1 ? AuditStatus.PASSED_LAST : AuditStatus.PASSED_1;
        case PASSED_1:
            return levels == 2 ? AuditStatus.PASSED_LAST : null;
        case REJECTED_2:
            return levels == 2 ? AuditStatus.PASSED_1 : null;
        default:
            return null;
        }
    }

}
