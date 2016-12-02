package org.truenewx.support.audit.service.state;

import java.util.Date;

import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 提交申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SubmitApplymentAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends UpdateApplymentAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.SUBMIT;
    }

    @Override
    public AuditStatus[] getStates() {
        return new AuditStatus[] { AuditStatus.CANCELED, AuditStatus.UNAPPLIED };
    }

    @Override
    public AuditStatus getNextState(final AuditStatus state, final Object context) {
        return AuditStatus.PENDING;
    }

    @Override
    protected Date getApplyTime() {
        return new Date();
    }

}
