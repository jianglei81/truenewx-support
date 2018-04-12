package org.truenewx.support.audit.service.state;

import org.springframework.stereotype.Service;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
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
    public AuditStatus[] getStates() {
        return new AuditStatus[] { AuditStatus.PENDING, AuditStatus.REJECTED_1 };
    }

    @Override
    public AuditStatus getNextState(final AuditStatus state, final Object context) {
        return AuditStatus.CANCELED;
    }

    @Override
    public U execute(final Long key, final Object context) throws HandleableException {
        final int applicantId = (Integer) context;
        final U entity = load(applicantId, key);
        entity.setStatus(AuditStatus.CANCELED);
        this.dao.save(entity);
        return entity;
    }

}
