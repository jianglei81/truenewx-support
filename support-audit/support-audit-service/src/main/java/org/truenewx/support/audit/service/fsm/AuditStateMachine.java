package org.truenewx.support.audit.service.fsm;

import org.truenewx.service.fsm.StateMachine;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核状态机
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuditStateMachine<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends StateMachine<U, Long, AuditState, AuditTransition, AuditUserIdentity> {

}
