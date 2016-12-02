package org.truenewx.support.audit.service.state;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.service.fsm.StateMachineImpl;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.service.AuditApplymentUnityService;

/**
 * 审核状态机实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditStateMachineImpl
        extends StateMachineImpl<Long, AuditStatus, AuditTransition, AuditEvent>
        implements AuditStateMachine, ContextInitializedBean {

    @Override
    @SuppressWarnings("unchecked")
    public void afterInitialized(final ApplicationContext context) throws Exception {
        setStartState(AuditStatus.UNAPPLIED);
        setStateGetter(context.getBean(AuditApplymentUnityService.class));
    }

}
