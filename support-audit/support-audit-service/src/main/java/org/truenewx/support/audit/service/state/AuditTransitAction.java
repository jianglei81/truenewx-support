package org.truenewx.support.audit.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.service.ServiceSupport;
import org.truenewx.service.fsm.TransitAction;
import org.truenewx.support.audit.data.dao.AuditApplymentUnityDao;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 审核转变动作
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
abstract class AuditTransitAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ServiceSupport implements TransitAction<Long, AuditStatus, AuditTransition> {

    protected AuditApplymentUnityDao<U, T, A> dao;

    @Autowired
    public void setDao(final AuditApplymentUnityDao<U, T, A> dao) {
        this.dao = dao;
    }

    @SuppressWarnings("unchecked")
    protected AuditPolicy<U, T, A> getPolicy(final T type) {
        return getService(AuditApplymentUnityService.class).getPolicy(type);
    }

}
