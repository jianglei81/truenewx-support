package org.truenewx.support.audit.service.state;

import org.truenewx.service.fsm.TransitEvent;
import org.truenewx.support.audit.data.enums.AuditTransition;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;

/**
 * 审核事件
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditEvent extends TransitEvent<Long, AuditTransition> {

    public AuditEvent(final long id, final AuditTransition transition, final Object context) {
        super(id, transition, context);
    }

    /**
     * 创建撤销事件实例
     *
     * @param applymentId
     *            申请实体id
     * @param applicantId
     *            申请者id
     * @return 撤销事件实例
     */
    public static AuditEvent newCancelInstance(final long applymentId, final int applicantId) {
        return new AuditEvent(applymentId, AuditTransition.CANCEL, applicantId);
    }

    /**
     * 创建修改申请事件实例
     *
     * @param applymentId
     *            申请实体id
     * @param model
     *            提交模型
     * @return 修改申请事件实例
     */
    public static AuditEvent newUpdateInstance(final long applymentId,
            final AuditApplymentSubmitModel<?> model) {
        return new AuditEvent(applymentId, AuditTransition.UPDATE, model);
    }

    /**
     * 创建提交申请事件实例
     *
     * @param applymentId
     *            申请实体id
     * @param model
     *            提交模型
     * @param first
     *            是否首次提交，false-重新提交申请
     * @return 提交申请事件实例
     */
    public static AuditEvent newSubmitInstance(final long applymentId,
            final AuditApplymentSubmitModel<?> model, final boolean first) {
        final AuditTransition transition = first ? AuditTransition.SUBMIT : AuditTransition.REAPPLY;
        return new AuditEvent(applymentId, transition, model);
    }

    /**
     * 创建审核事件实例
     *
     * @param applymentId
     *            申请实体id
     * @param passed
     *            是否审核通过
     * @param auditor
     *            审核者
     * @param attitude
     *            审核态度
     * @return 审核事件实例
     */
    public static AuditEvent newAuditInstance(final long applymentId, final boolean passed,
            final Auditor<?> auditor, final String attitude) {
        final AuditTransition transition = passed ? AuditTransition.PASS : AuditTransition.REJECT;
        return new AuditEvent(applymentId, transition,
                new AuditOperateContext<>(auditor, attitude));
    }

}
