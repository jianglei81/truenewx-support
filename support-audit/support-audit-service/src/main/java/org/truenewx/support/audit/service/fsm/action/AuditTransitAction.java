package org.truenewx.support.audit.service.fsm.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.service.ServiceSupport;
import org.truenewx.service.fsm.TransitAction;
import org.truenewx.support.audit.data.dao.AuditApplymentUnityDao;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 审核转变动作
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            申请实体类型
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
public abstract class AuditTransitAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ServiceSupport implements TransitAction<U, Long, AuditState, AuditTransition> {

    protected AuditApplymentUnityDao<U, T, A> dao;

    @Autowired
    public void setDao(final AuditApplymentUnityDao<U, T, A> dao) {
        this.dao = dao;
    }

    @SuppressWarnings("unchecked")
    protected final AuditApplymentUnityService<U, T, A> getService() {
        return getService(AuditApplymentUnityService.class);
    }

    protected final AuditPolicy<U, T, A> loadPolicy(final T type) {
        return getService().loadPolicy(type);
    }

}
