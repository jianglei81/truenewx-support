package org.truenewx.support.audit.data.param;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.truenewx.data.query.QueryParameterImpl;
import org.truenewx.support.audit.data.model.AuditState;

/**
 * 审核申请单体查询参数
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuditApplymentUnityQueryParameter extends QueryParameterImpl {

    private AuditState[] statuses;
    private Set<Integer> applicantIds;
    private Map<String, Object> contentParams;

    public AuditState[] getStatuses() {
        return this.statuses;
    }

    public void setStatuses(final AuditState... statuses) {
        this.statuses = statuses;
    }

    public Set<Integer> getApplicantIds() {
        return this.applicantIds;
    }

    public void setApplicantIds(final Set<Integer> applicantIds) {
        this.applicantIds = applicantIds;
    }

    public Map<String, Object> getContentParams() {
        return this.contentParams;
    }

    public void addContentParam(final String name, final Object value) {
        if (this.contentParams == null) {
            this.contentParams = new HashMap<>();
        }
        this.contentParams.put(name, value);
    }

}
