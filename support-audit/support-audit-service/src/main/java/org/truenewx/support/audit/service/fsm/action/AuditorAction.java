package org.truenewx.support.audit.service.fsm.action;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditLogUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
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
        extends AuditTransitAction<U, T, A> {

    private AuditLogEntityCreator<T, A> logEntityCreator;

    @Autowired
    public void setLogEntityCreator(final AuditLogEntityCreator<T, A> logEntityCreator) {
        this.logEntityCreator = logEntityCreator;
    }

    protected U load(final long id) throws BusinessException {
        return getService().load(id);
    }

    @SuppressWarnings("unchecked")
    protected T getApplymentTypeByContext(final Object context) {
        if (context != null) {
            final Class<T> typeClass = ClassUtil.getActualGenericType(getClass(), 1);
            if (context.getClass() == typeClass) {
                return (T) context;
            } else {
                final Object type = BeanUtil.getPropertyValue(context, "type");
                return getApplymentTypeByContext(type);
            }
        }
        return null;
    }

    @Override
    public U execute(final AuditUserIdentity userIdentity, final Long key, final Object context)
            throws HandleableException {
        @SuppressWarnings("unchecked")
        final AuditOperateContext<T> operateContext = (AuditOperateContext<T>) context;
        final String attitude = operateContext.getAttitude();
        if (getTransition() == AuditTransition.REJECT && StringUtils.isBlank(attitude)) {
            throw new BusinessException(AuditExceptionCodes.BLANK_REJECT_ATTITUDE);
        }

        final AuditorIdentity auditorIdentity = (AuditorIdentity) userIdentity;
        final U applyment = load(key);
        final T type = applyment.getType();
        final A auditor = getService().getAuditor(auditorIdentity);
        if (!auditor.isAuditable(type, applyment.getState().getLevel())) { // 无审核权限
            throw new BusinessException(AuditExceptionCodes.NO_AUDIT_AUTHORITY);
        }

        final AuditState newState = getEndState(applyment.getState(), context);
        final Date now = new Date();
        final AuditLogUnity<T, A> log = this.logEntityCreator.newLogEntity();
        log.setApplyment(applyment);
        applyment.getLogs().add(log);
        log.setAuditor(auditor);
        log.setAttitude(attitude);
        log.setNewStatus(newState);
        log.setCreateTime(now);
        applyment.setState(newState);
        applyment.setLastAuditTime(now.getTime());
        this.dao.save(applyment);

        final AuditPolicy<U, T, A> policy = loadPolicy(type);
        switch (newState) {
        case PASSED_LAST:
            policy.onPassed(applyment, operateContext.getAddition());
            break;
        case REJECTED_1:
            policy.onRejected(applyment);
            break;
        default:
            break;
        }
        return applyment;
    }
}
