package org.truenewx.support.audit.service.policy;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.service.ServiceSupport;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;

/**
 * 抽象的审核方针
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            审核申请实体类型
 * @param <T>
 *            申请类型枚举类型
 * @param <A>
 *            审核者类型
 */
public abstract class AbstractAuditPolicy<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends ServiceSupport implements AuditPolicy<U, T, A> {

    @Override
    public void transform(final AuditApplymentSubmitModel<U> submitModel, final U unity)
            throws HandleableException {
        unity.setApplicantId(submitModel.getApplicantId());
        unity.setContentString(this.toContentString(submitModel.getContent()));
        unity.setReason(submitModel.getReason());
    }

    protected String toContentString(final Object content) {
        return JsonUtil.bean2Json(content);
    }

    @Override
    public void prepareQueryParameter(final AuditApplymentUnityQueryParameter parameter)
            throws BusinessException {
    }

    @Override
    public Object parseContent(final String contentString) {
        return JsonUtil.json2Bean(contentString);
    }

    @Override
    public void onPassed(final U applyment, final Object addition) throws HandleableException {
    }

    @Override
    public void onRejected(final U applyment) throws HandleableException {
    }
}
