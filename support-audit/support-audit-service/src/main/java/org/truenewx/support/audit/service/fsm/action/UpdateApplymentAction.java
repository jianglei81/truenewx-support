package org.truenewx.support.audit.service.fsm.action;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
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
    public AuditState getEndState(final AuditState beginState, final Object condition) {
        return AuditState.UNAPPLIED;
    }

    @Override
    public U execute(final AuditUserIdentity userIdentity, final Long key, final Object context)
            throws HandleableException {
        @SuppressWarnings("unchecked")
        final AuditApplymentSubmitModel<U> model = (AuditApplymentSubmitModel<U>) context;
        final U unity = load(model.getApplicantId(), key);
        getService().transform(model, unity);
        unity.setState(getEndState(unity.getState(), context));
        unity.setApplyTime(getApplyTime());
        this.dao.save(unity);
        return unity;
    }

    protected Date getApplyTime() {
        return null;
    }

}
