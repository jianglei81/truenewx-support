package org.truenewx.support.audit.data.model;

import org.truenewx.core.annotation.Caption;

/**
 * 审核转变
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum AuditTransition {
    @Caption("撤销")
    CANCEL,

    @Caption("修改申请")
    UPDATE,

    @Caption("提交申请")
    SUBMIT,

    @Caption("通过")
    PASS,

    @Caption("拒绝")
    REJECT,

    @Caption("重新申请")
    REAPPLY;

}
