package org.truenewx.support.audit.data.model;

import java.util.Date;

import org.truenewx.data.model.unity.AbstractUnity;
import org.truenewx.data.validation.constraint.NotContainsSpecialChars;
import org.truenewx.support.audit.data.enums.AuditStatus;

/**
 * 抽象的审核日志单体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
public class AbstractAuditLogUnity<T extends Enum<T>, A extends Auditor<T>>
        extends AbstractUnity<Long> {

    private A auditor;

    /**
     * 审核操作后的新状态
     */
    private AuditStatus newStatus;

    @NotContainsSpecialChars
    private String attitude;

    private Date createTime;

    private AuditApplymentUnity<T, A> applyment;

    public A getAuditor() {
        return this.auditor;
    }

    public void setAuditor(final A auditor) {
        this.auditor = auditor;
    }

    /**
     *
     * @return 审核操作后的新状态
     */
    public AuditStatus getNewStatus() {
        return this.newStatus;
    }

    /**
     *
     * @param newStatus
     *            审核操作后的新状态
     */
    public void setNewStatus(final AuditStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getAttitude() {
        return this.attitude;
    }

    public void setAttitude(final String attitude) {
        this.attitude = attitude;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public AuditApplymentUnity<T, A> getApplyment() {
        return this.applyment;
    }

    public void setApplyment(final AuditApplymentUnity<T, A> applyment) {
        this.applyment = applyment;
    }

}
