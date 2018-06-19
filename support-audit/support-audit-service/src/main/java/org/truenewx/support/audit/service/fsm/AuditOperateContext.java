package org.truenewx.support.audit.service.fsm;

/**
 * 审核操作上下文
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditOperateContext<T extends Enum<T>> {
    private T type;
    private String attitude;
    private Object addition;

    public AuditOperateContext(final T type, final String attitude) {
        this.type = type;
        this.attitude = attitude;
    }

    public T getType() {
        return this.type;
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
