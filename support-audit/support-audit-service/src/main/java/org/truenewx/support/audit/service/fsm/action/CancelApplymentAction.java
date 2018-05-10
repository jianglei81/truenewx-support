package org.truenewx.support.audit.service.fsm.action;

import org.springframework.stereotype.Service;
import org.truenewx.core.exception.HandleableException;
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
    public AuditState[] getStates() {
        return new AuditState[] { AuditState.PENDING, AuditState.REJECTED_1 };
    }

    @Override
    public AuditState getNextState(final AuditState state, final Object context) {
        return AuditState.CANCELED;
    }

    @Override
    public U execute(final Long key, final Object context) throws HandleableException {
        final int applicantId = (Integer) context;
        final U entity = load(applicantId, key);
        entity.setState(AuditState.CANCELED);
        this.dao.save(entity);
        return entity;
    }

}
