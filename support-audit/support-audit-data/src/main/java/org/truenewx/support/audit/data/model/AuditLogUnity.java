package org.truenewx.support.audit.data.model;

/**
 * 审核日志单体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
public class AuditLogUnity<T extends Enum<T>, A extends Auditor<T>>
        extends AbstractAuditLogUnity<T, A> implements Comparable<AuditLogUnity<T, A>> {

    @Override
    public int compareTo(final AuditLogUnity<T, A> other) {
        return getCreateTime().compareTo(other.getCreateTime());
    }

}
