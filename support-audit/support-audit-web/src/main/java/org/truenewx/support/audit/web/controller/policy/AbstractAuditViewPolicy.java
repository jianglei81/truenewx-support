package org.truenewx.support.audit.web.controller.policy;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 抽象的审核视图方针
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            审核申请实体类型
 * @param <T>
 *            审核状态枚举类型
 * @param <A>
 *            审核者类型
 */
public abstract class AbstractAuditViewPolicy<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        implements AuditViewPolicy<U, T, A> {

    @Autowired
    private AuditApplymentUnityService<U, T, A> service;

    protected AuditPolicy<U, T, A> getAuditPolicy() {
        return this.service.getPolicy(this.getType());
    }

    @Override
    public String[] getListContentFields() {
        return null;
    }

    @Override
    public Object getListContent(final U applyment) {
        return null;
    }

    @Override
    public Map<String, Object> getDetailContentModel(final U applyment) {
        return null;
    }

    @Override
    public ModelAndView preparePass(final U applyment) throws HandleableException {
        return null;
    }

    @Override
    public Object getPassAddition(final HttpServletRequest request) {
        return null;
    }
}
