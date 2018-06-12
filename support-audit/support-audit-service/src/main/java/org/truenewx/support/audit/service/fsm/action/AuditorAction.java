package org.truenewx.support.audit.service.fsm.action;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditLogUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.model.AuditorIdentity;
import org.truenewx.support.audit.service.AuditExceptionCodes;
import org.truenewx.support.audit.service.AuditLogEntityCreator;
import org.truenewx.support.audit.service.fsm.AuditOperateContext;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 审核者动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
abstract class AuditorAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AuditTransitAction<U, T, A, AuditorIdentity> {

    private AuditLogEntityCreator<T, A> logEntityCreator;

    @Autowired
    public void setLogEntityCreator(final AuditLogEntityCreator<T, A> logEntityCreator) {
        this.logEntityCreator = logEntityCreator;
    }

    @Override
    public boolean execute(final AuditorIdentity userIdentity, final U entity, final Object context)
            throws HandleableException {
        @SuppressWarnings("unchecked")
        final AuditOperateContext<T> operateContext = (AuditOperateContext<T>) context;
        final String attitude = operateContext.getAttitude();
        if (getTransition() == AuditTransition.REJECT && StringUtils.isBlank(attitude)) {
            throw new BusinessException(AuditExceptionCodes.BLANK_REJECT_ATTITUDE);
        }

        final T type = entity.getType();
        final A auditor = getService().getAuditor(userIdentity);
        if (!auditor.isAuditable(type, entity.getState().getLevel())) { // 无审核权限
            throw new BusinessException(AuditExceptionCodes.NO_AUDIT_AUTHORITY);
        }

        final AuditState newState = getEndState(entity.getState(), context);
        final Date now = new Date();
        final AuditLogUnity<T, A> log = this.logEntityCreator.newLogEntity();
        log.setApplyment(entity);
        entity.getLogs().add(log);
        log.setAuditor(auditor);
        log.setAttitude(attitude);
        log.setNewState(newState);
        log.setCreateTime(now);
        entity.setState(newState);
        entity.setLastAuditTime(now.getTime());
        getDao().save(entity);

        final AuditPolicy<U, T, A> policy = loadPolicy(type);
        switch (newState) {
        case PASSED_LAST:
            policy.onPassed(entity, operateContext.getAddition());
            return true;
        case REJECTED_1:
            policy.onRejected(entity);
            return true;
        default:
            return false;
        }
    }
}
