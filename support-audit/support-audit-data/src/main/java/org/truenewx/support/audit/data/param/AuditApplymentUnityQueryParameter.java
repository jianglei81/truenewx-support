package org.truenewx.support.audit.data.param;

import java.util.Set;

import org.truenewx.data.query.QueryParameterImpl;
import org.truenewx.support.audit.data.model.AuditStatus;

/**
 * 审核申请单体查询参数
 *
 * @author jianglei
 * @version 1.0.0 2014年9月27日
 * @since JDK 1.8
 */
public class AuditApplymentUnityQueryParameter extends QueryParameterImpl {

    private Set<Integer> applicantIds;
    private String keyword;
    private AuditStatus[] statuses;

    public Set<Integer> getApplicantIds() {
        return this.applicantIds;
    }

    public void setApplicantIds(final Set<Integer> applicantIds) {
        this.applicantIds = applicantIds;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public AuditStatus[] getStatuses() {
        return this.statuses;
    }

    public void setStatuses(final AuditStatus... statuses) {
        this.statuses = statuses;
    }

}
