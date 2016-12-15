package org.truenewx.support.audit.service.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
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
@Service
public class AuditStateMachineImpl
        extends StateMachineImpl<Long, AuditStatus, AuditTransition, AuditEvent>
        implements AuditStateMachine, ContextInitializedBean {

    @Override
    @SuppressWarnings("unchecked")
    public void afterInitialized(final ApplicationContext context) throws Exception {
        setStartState(AuditStatus.UNAPPLIED);
        setStateGetter(context.getBean(AuditApplymentUnityService.class));
        @SuppressWarnings("rawtypes")
        final Map<String, AuditTransitAction> beans = context
                .getBeansOfType(AuditTransitAction.class);
        final List<AuditTransitAction<?, ?, ?>> actions = new ArrayList<>();
        for (final AuditTransitAction<?, ?, ?> action : beans.values()) {
            actions.add(action);
        }
        setTransitActions(actions);
    }

}
