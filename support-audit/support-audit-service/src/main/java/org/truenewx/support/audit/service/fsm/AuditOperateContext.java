package org.truenewx.support.audit.service.fsm;

/**
 * 审核操作上下文
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditOperateContext {

    private String attitude;
    private Object addition;

    public AuditOperateContext(final String attitude) {
        this.attitude = attitude;
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
