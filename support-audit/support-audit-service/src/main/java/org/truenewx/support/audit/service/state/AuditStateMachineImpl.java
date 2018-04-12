package org.truenewx.support.audit.service.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.service.fsm.AbstractStateMachine;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;

/**
 * 审核状态机实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class AuditStateMachineImpl<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AbstractStateMachine<U, Long, AuditStatus, AuditTransition, AuditEvent>
        implements AuditStateMachine<U, T, A>, ContextInitializedBean {

    private AuditApplymentUnityService<U, T, A> service;

    @Override
    @SuppressWarnings("unchecked")
    public void afterInitialized(final ApplicationContext context) throws Exception {
        setStartState(AuditStatus.UNAPPLIED);
        this.service = context.getBean(AuditApplymentUnityService.class);
        @SuppressWarnings("rawtypes")
        final Map<String, AuditTransitAction> beans = context
                .getBeansOfType(AuditTransitAction.class);
        final List<AuditTransitAction<U, T, A>> actions = new ArrayList<>();
        for (final AuditTransitAction<U, T, A> action : beans.values()) {
            actions.add(action);
        }
        setTransitActions(actions);
    }

    @Override
    protected AuditStatus getState(final Long key) {
        final U unity = this.service.find(key);
        if (unity != null) {
            return unity.getStatus();
        }
        return null;
    }

}
