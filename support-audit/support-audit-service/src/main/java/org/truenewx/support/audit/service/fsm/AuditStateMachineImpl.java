package org.truenewx.support.audit.service.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.service.fsm.EventDrivenStateMachine;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;
import org.truenewx.support.audit.service.fsm.action.AuditTransitAction;

import com.google.common.eventbus.Subscribe;

/**
 * 审核状态机实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class AuditStateMachineImpl<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends
        EventDrivenStateMachine<U, Long, AuditState, AuditTransition, AuditUserIdentity, AuditEvent>
        implements AuditStateMachine<U, T, A>, ContextInitializedBean {

    @Override
    @SuppressWarnings("unchecked")
    public void afterInitialized(final ApplicationContext context) throws Exception {
        setStartState(AuditState.UNAPPLIED);
        @SuppressWarnings("rawtypes")
        final Map<String, AuditTransitAction> beans = context
                .getBeansOfType(AuditTransitAction.class);
        final List<AuditTransitAction<U, T, A, AuditUserIdentity>> actions = new ArrayList<>();
        for (final AuditTransitAction<U, T, A, AuditUserIdentity> action : beans.values()) {
            actions.add(action);
        }
        setTransitActions(actions);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected U loadEntity(final AuditUserIdentity userIdentity, final Long key,
            final Object context) throws BusinessException {
        return (U) getService(AuditApplymentUnityService.class).load(key);
    }

    @Override
    protected AuditState getState(final U entity) {
        return entity.getState();
    }

    @Override
    protected Object getCondition(final AuditUserIdentity userIdentity, final U entity,
            final Object context) {
        if (context instanceof AuditOperateContext) {
            return entity.getType();
        }
        return null;
    }

    @Override
    @Subscribe
    public void onEvent(AuditEvent event) throws HandleableException {
        super.onEvent(event);
    }

}
