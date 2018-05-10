package org.truenewx.support.audit.data.model;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import org.truenewx.data.annotation.Redundant;
import org.truenewx.data.model.unity.AbstractUnity;
import org.truenewx.data.validation.constraint.NotContainsAngleBracket;
import org.truenewx.data.validation.constraint.NotContainsSpecialChars;
import org.truenewx.data.validation.constraint.NotContainsSqlChars;

/**
 * 抽象的审核申请单体
 *
 * @author jianglei
 * @version 1.0.0 2014年10月22日
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
public abstract class AbstractAuditApplymentUnity<T extends Enum<T>, A extends Auditor<T>>
        extends AbstractUnity<Long> {

    private T type;
    private AuditState state;
    @NotContainsSpecialChars
    private String reason;
    @NotContainsAngleBracket
    @NotContainsSqlChars
    private String contentString;
    private int applicantId;
    private Date createTime;
    private Date applyTime;
    @Redundant
    private long lastAuditTime;
    private Collection<? extends AuditLogUnity<T, A>> logs = new TreeSet<>();

    public T getType() {
        return this.type;
    }

    public void setType(final T type) {
        this.type = type;
    }

    public AuditState getState() {
        return this.state;
    }

    public void setState(final AuditState state) {
        this.state = state;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getContentString() {
        return this.contentString;
    }

    public void setContentString(final String contentString) {
        this.contentString = contentString;
    }

    public int getApplicantId() {
        return this.applicantId;
    }

    public void setApplicantId(final int applicantId) {
        this.applicantId = applicantId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public Date getApplyTime() {
        return this.applyTime;
    }

    public void setApplyTime(final Date applyTime) {
        this.applyTime = applyTime;
    }

    public long getLastAuditTime() {
        return this.lastAuditTime;
    }

    public void setLastAuditTime(final long lastAuditTime) {
        this.lastAuditTime = lastAuditTime;
    }

    @SuppressWarnings("unchecked")
    public <L extends AuditLogUnity<T, A>> Collection<L> getLogs() {
        return (Collection<L>) this.logs;
    }

    protected <L extends AuditLogUnity<T, A>> void setLogs(final Collection<L> logs) {
        this.logs = logs;
    }

}
