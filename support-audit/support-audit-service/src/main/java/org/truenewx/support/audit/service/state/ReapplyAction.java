package org.truenewx.support.audit.service.state;

import org.springframework.stereotype.Service;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 重新申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
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
