package org.truenewx.support.audit.service.state;

import org.truenewx.support.audit.data.enums.AuditStatus;
import org.truenewx.support.audit.data.enums.AuditTransition;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 重新申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ReapplyAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends SubmitApplymentAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.REAPPLY;
    }

    @Override
    public AuditStatus[] getStates() {
        return new AuditStatus[] { AuditStatus.REJECTED_1 };
    }

}
