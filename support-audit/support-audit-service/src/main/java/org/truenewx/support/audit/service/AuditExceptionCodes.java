package org.truenewx.support.audit.service;

/**
 * 审核异常错误码集
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditExceptionCodes {

    private AuditExceptionCodes() {
    }

    /**
     * 不存在的申请
     */
    public static final String NONEXISTENT_APPLYMENT = "error.audit.nonexistent_applyment";

    /**
     * 审核拒绝时必须填写说明
     */
    public static final String BLANK_REJECT_ATTITUDE = "error.audit.blank_reject_attitude";

    /**
     * 只有未提交的申请才可修改
     */
    public static final String ONLY_SUBMITTABLE_APPLYMENT_IS_UPDATABLE = "error.audit.only_submittable_applyment_is_updatable";

    /**
     * 无审核权限
     */
    public static final String NO_AUDIT_AUTHORITY = "error.audit.no_audit_authority";

}
