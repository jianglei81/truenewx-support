package org.truenewx.support.audit.data.model;

/**
 * 审核申请者标识
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ApplicantIdentity extends AuditUserIdentity {

    private static final long serialVersionUID = 1715237616991736872L;

    public ApplicantIdentity(final int value) {
        super(value);
    }

}
