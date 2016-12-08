package org.truenewx.support.audit.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;
import org.truenewx.support.audit.service.AuditApplymentUnityService;

/**
 * 审核控制器支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AuditControllerSupport<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>> {
    @Autowired
    protected AuditApplymentUnityService<U, T, A> service;

    public abstract ModelAndView list(final AuditApplymentUnityQueryParameter parameter);

    public abstract ModelAndView detail(long applymentId);

    public abstract ModelAndView toPass(long applymentId, String attitude);

    public abstract ModelAndView pass(long applymentId, String attitude);

    public abstract ModelAndView reject(long applymentId, String attitude);

}
