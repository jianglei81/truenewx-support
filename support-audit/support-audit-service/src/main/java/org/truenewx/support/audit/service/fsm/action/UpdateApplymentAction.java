package org.truenewx.support.audit.service.fsm.action;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.user.UserIdentity;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;

/**
 * 修改申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class UpdateApplymentAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ApplicantAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.UPDATE;
    }

    @Override
    public AuditState[] getStates() {
        return new AuditState[] { AuditState.CANCELED, AuditState.UNAPPLIED,
                AuditState.REJECTED_1 };
    }

    @Override
    public AuditState getNextState(final UserIdentity userIdentity, final AuditState state) {
        return AuditState.UNAPPLIED;
    }

    @Override
    public U execute(UserIdentity userIdentity, final Long key, final Object context) throws HandleableException {
        @SuppressWarnings("unchecked")
        final AuditApplymentSubmitModel<U> model = (AuditApplymentSubmitModel<U>) context;
        final U unity = load(model.getApplicantId(), key);
        getService().transform(model, unity);
        unity.setState(getNextState(context, unity.getState()));
        unity.setApplyTime(getApplyTime());
        this.dao.save(unity);
        return unity;
    }

    protected Date getApplyTime() {
        return null;
    }

}
