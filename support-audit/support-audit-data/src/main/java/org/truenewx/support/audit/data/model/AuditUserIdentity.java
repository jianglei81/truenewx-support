package org.truenewx.support.audit.data.model;

import org.truenewx.data.user.UserIdentity;

/**
 * 审核用户标识
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AuditUserIdentity implements UserIdentity {

    private static final long serialVersionUID = -1201874856807276486L;

    private int value;

    public AuditUserIdentity(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
