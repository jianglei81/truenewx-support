package org.truenewx.support.audit.service.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    private Object content;
    private String reason;

    public int getApplicantId() {
        return this.applicantId;
    }

    public void setApplicantId(final int applicantId) {
        this.applicantId = applicantId;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(final Object content) {
        this.content = content;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    /**
     * 合并新内容，以已有内容为优先
     *
     * @param content
     *            新内容
     */
    @SuppressWarnings("unchecked")
    public void mergeContent(final Map<String, Object> content) {
        if (content != null && !content.isEmpty()) {
            if (this.content == null) {
                this.content = new HashMap<>(content);
            } else {
                for (final Entry<String, Object> entry : content.entrySet()) {
                    final String key = entry.getKey();
                    if (!((Map<String, Object>) this.content).containsKey(key)) { // 原本不包含才加入
                        ((Map<String, Object>) this.content).put(key, entry.getValue());
                    }
                }
            }
        }
    }
}
