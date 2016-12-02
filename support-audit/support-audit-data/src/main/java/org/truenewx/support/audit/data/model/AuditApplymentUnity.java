package org.truenewx.support.audit.data.model;

import java.util.List;
import java.util.Map;

import org.truenewx.core.util.JsonUtil;
import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 审核申请单体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
public class AuditApplymentUnity<T extends Enum<T>, A extends Auditor<T>>
        extends AbstractAuditApplymentUnity<T, A> implements OwnedUnity<Long, Integer> {

    @Override
    public Integer getOwner() {
        return getApplicantId();
    }

    /**
     * 获取最近的审核日志
     *
     * @return 最近的审核日志
     */
    @SuppressWarnings("unchecked")
    public <L extends AuditLogUnity<T, A>> L getLastLog() {
        AuditLogUnity<T, A> last = null;
        final List<AuditLogUnity<T, A>> logs = getLogs();
        if (logs != null) {
            for (final AuditLogUnity<T, A> log : getLogs()) {
                if (last == null || log.getCreateTime().after(last.getCreateTime())) {
                    last = log;
                }
            }
        }
        return (L) last;
    }

    /**
     *
     * @return 是否可提交审核
     */
    public boolean isSubmittable() {
        final AuditStatus status = getStatus();
        return status == AuditStatus.UNAPPLIED || status == AuditStatus.REJECTED_1
                || status == AuditStatus.CANCELED;
    }

    /**
     *
     * @return 是否已进行过审核操作，为true不表示已审核完成
     */
    public boolean isAudited() {
        final AuditStatus status = getStatus();
        return status == AuditStatus.PASSED_1 || status == AuditStatus.PASSED_LAST
                || status == AuditStatus.REJECTED_1 || status == AuditStatus.REJECTED_2;
    }

    @SuppressWarnings("unchecked")
    public <V> V getContent(final String name) {
        final Map<String, Object> content = JsonUtil.json2Map(getContentString());
        return content == null ? null : (V) content.get(name);
    }
}
