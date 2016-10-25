package org.truenewx.support.audit.service.state;

import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核操作上下文
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            审核类型枚举类型
 * @param <A>
 *            审核者类型
 */
public class AuditOperateContext<T extends Enum<T>, A extends Auditor<T>> {

    private A auditor;
    private String attitude;
    private Object addition;

    public AuditOperateContext(final A auditor, final String attitude) {
        this.auditor = auditor;
        this.attitude = attitude;
    }

    public A getAuditor() {
        return this.auditor;
    }

    public String getAttitude() {
        return this.attitude;
    }

    public Object getAddition() {
        return this.addition;
    }

    public void setAddition(final Object addition) {
        this.addition = addition;
    }

}
