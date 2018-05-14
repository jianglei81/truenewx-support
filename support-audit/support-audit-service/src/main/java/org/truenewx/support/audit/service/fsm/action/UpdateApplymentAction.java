package org.truenewx.support.audit.service.fsm.action;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.ApplicantIdentity;
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
    public AuditState[] getBeginStates() {
        return new AuditState[] { AuditState.PENDING };
    }

    @Override
    public AuditState getEndState(final AuditState beginState, final Object condition) {
        return AuditState.UNAPPLIED;
    }

    @Override
    public boolean execute(final ApplicantIdentity userIdentity, final U entity,
            final Object context) throws HandleableException {
        @SuppressWarnings("unchecked")
        final AuditApplymentSubmitModel<U> model = (AuditApplymentSubmitModel<U>) context;
        getService().transform(model, entity);
        entity.setState(AuditState.UNAPPLIED);
        entity.setApplyTime(getApplyTime());
        this.dao.save(entity);
        return true;
    }

    protected Date getApplyTime() {
        return null;
    }

}
