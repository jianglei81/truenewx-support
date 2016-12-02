package org.truenewx.support.audit.service.state;

import org.truenewx.service.fsm.StateMachine;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.AuditTransition;

/**
 * 审核状态机
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuditStateMachine
        extends StateMachine<Long, AuditStatus, AuditTransition, AuditEvent> {

}
