package org.truenewx.support.audit.service.fsm;

import org.truenewx.service.fsm.TransitEvent;
import org.truenewx.support.audit.data.model.ApplicantIdentity;
import org.truenewx.support.audit.data.model.AuditTransition;
import org.truenewx.support.audit.data.model.AuditUserIdentity;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;

/**
 * 审核事件
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditEvent extends TransitEvent<AuditUserIdentity, Long, AuditTransition> {

    public AuditEvent(final AuditUserIdentity userIdentity, final Long key,
            final AuditTransition transition) {
        super(userIdentity, key, transition);
    }

    public AuditEvent(final AuditUserIdentity userIdentity, final Long key,
            final AuditTransition transition, final Object context) {
        super(userIdentity, key, transition, context);
    }

    /**
     * 创建提交申请事件实例
     *
     * @param applicantIdentity
     *            申请实体id
     * @param applymentId
     *            申请实体id
     * @param model
     *            提交模型
     * @param first
     *            是否首次提交，false-重新提交申请
     * @return 提交申请事件实例
     */
    public static AuditEvent newSubmitInstance(final ApplicantIdentity applicantIdentity,
            final long applymentId, final AuditApplymentSubmitModel<?> model, final boolean first) {
        final AuditTransition transition = first ? AuditTransition.SUBMIT : AuditTransition.REAPPLY;
        return new AuditEvent(applicantIdentity, applymentId, transition, model);
    }

    /**
     * 创建修改申请事件实例
     *
     * @param applicantIdentity
     *            申请者标识
     * @param applymentId
     *            申请实体id
     * @param model
     *            提交模型
     *
     * @return 修改申请事件实例
     */
    public static AuditEvent newUpdateInstance(final ApplicantIdentity applicantIdentity,
            final long applymentId, final AuditApplymentSubmitModel<?> model) {
        return new AuditEvent(applicantIdentity, applymentId, AuditTransition.UPDATE, model);
    }

    /**
     * 创建撤销事件实例
     *
     * @param applicantIdentity
     *            申请者标识
     * @param applymentId
     *            申请实体id
     *
     * @return 撤销事件实例
     */
    public static AuditEvent newCancelInstance(final ApplicantIdentity applicantIdentity,
            final long applymentId) {
        return new AuditEvent(applicantIdentity, applymentId, AuditTransition.CANCEL);
    }

}
