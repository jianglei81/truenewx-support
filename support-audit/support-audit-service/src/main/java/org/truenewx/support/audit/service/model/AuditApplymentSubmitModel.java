package org.truenewx.support.audit.service.model;

import java.util.Map;

import org.truenewx.data.model.SubmitModel;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;

/**
 * 审核申请提交模型
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型枚举类型
 * @param <A>
 *            审核者类型
 */
public class AuditApplymentSubmitModel<U extends AuditApplymentUnity<?, ?>>
        implements SubmitModel<U> {
    private int applicantId;
    private Map<String, Object> content;
    private String reason;

    /**
     * @return 申请者id
     */
    public int getApplicantId() {
        return this.applicantId;
    }

    /**
     * @param applicantId
     *            申请者id
     */
    public void setApplicantId(final int applicantId) {
        this.applicantId = applicantId;
    }

    /**
     * @return 审核内容
     */
    public Map<String, Object> getContent() {
        return this.content;
    }

    /**
     * @param content
     *            审核内容
     */
    public void setContent(final Map<String, Object> content) {
        this.content = content;
    }

    /**
     * @return 申请原因
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * @param reason
     *            申请原因
     */
    public void setReason(final String reason) {
        this.reason = reason;
    }

}
