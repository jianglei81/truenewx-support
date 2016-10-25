package org.truenewx.support.audit.service.state;

import java.util.Date;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.enums.AuditStatus;
import org.truenewx.support.audit.data.enums.AuditTransition;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 修改申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UpdateApplymentAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ApplicantAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.UPDATE;
    }

    @Override
    public AuditStatus[] getStates() {
        return new AuditStatus[] { AuditStatus.CANCELED, AuditStatus.UNAPPLIED,
                AuditStatus.REJECTED_1 };
    }

    @Override
    public AuditStatus getNextState(final AuditStatus state, final Object context) {
        return AuditStatus.UNAPPLIED;
    }

    @Override
    public void execute(final Long key, final Object context) throws HandleableException {
        @SuppressWarnings("unchecked")
        final AuditApplymentSubmitModel<U> model = (AuditApplymentSubmitModel<U>) context;
        final U entity = get(model.getApplicantId(), key);
        final AuditPolicy<U, T, A> policy = getPolicy(entity.getType());
        if (policy != null) {
            policy.transform(model, entity);
            entity.setStatus(getNextState(entity.getStatus(), context));
            entity.setApplyTime(getApplyTime());
            this.dao.save(entity);
        }
    }

    protected Date getApplyTime() {
        return null;
    }

}
