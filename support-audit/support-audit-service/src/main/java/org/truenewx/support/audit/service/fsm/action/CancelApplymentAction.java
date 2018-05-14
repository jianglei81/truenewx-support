package org.truenewx.support.audit.service.fsm.action;

import org.springframework.stereotype.Service;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.ApplicantIdentity;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 撤销申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
@Service
public class CancelApplymentAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ApplicantAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.CANCEL;
    }

    @Override
    public AuditState[] getBeginStates() {
        return new AuditState[] { AuditState.PENDING };
    }

    @Override
    public AuditState getEndState(final AuditState beginState, final Object condition) {
        return AuditState.CANCELED;
    }

    @Override
    public boolean execute(final ApplicantIdentity userIdentity, final U entity,
            final Object context) throws HandleableException {
        if (entity.getApplicantId() == userIdentity.getValue()) {
            entity.setState(AuditState.CANCELED);
            this.dao.save(entity);
            return true;
        }
        return false;
    }

}
