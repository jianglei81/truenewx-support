package org.truenewx.support.audit.service.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.data.finder.UnitaryEntityFinder;
import org.truenewx.service.fsm.EventDrivenStateMachine;
import org.truenewx.support.audit.data.dao.AuditApplymentUnityDao;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.fsm.action.AuditTransitAction;

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

    private AuditApplymentUnityDao<U, T, A> dao;

    @Override
    @SuppressWarnings("unchecked")
    public void afterInitialized(final ApplicationContext context) throws Exception {
        setStartState(AuditState.UNAPPLIED);
        this.dao = context.getBean(AuditApplymentUnityDao.class);
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
    protected UnitaryEntityFinder<U, Long> getFinder() {
        return this.dao;
    }

    @Override
    protected AuditState getState(final U entity) {
        return entity.getState();
    }

    @Override
    protected Object getCondition(final AuditUserIdentity userIdentity, final U entity,
            final AuditTransition transition, final Object context) {
        if (context instanceof AuditOperateContext) {
            return entity.getType();
        }
        return null;
    }

}
