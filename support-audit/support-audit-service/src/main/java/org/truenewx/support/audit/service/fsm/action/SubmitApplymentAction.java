package org.truenewx.support.audit.service.fsm.action;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 提交申请动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class SubmitApplymentAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends UpdateApplymentAction<U, T, A> {

    @Override
    public AuditTransition getTransition() {
        return AuditTransition.SUBMIT;
    }

    @Override
    public AuditState getEndState(final AuditState beginState, final Object condition) {
        return AuditState.PENDING;
    }

    @Override
    protected Date getApplyTime() {
        return new Date();
    }

}
