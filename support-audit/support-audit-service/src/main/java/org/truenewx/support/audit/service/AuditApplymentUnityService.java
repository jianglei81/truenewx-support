package org.truenewx.support.audit.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.query.QueryResult;
import org.truenewx.service.unity.OwnedUnityService;
import org.truenewx.service.unity.UnityService;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;
import org.truenewx.support.audit.service.policy.AuditPolicy;

/**
 * 审核申请单体服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            审核申请单体类型
 * @param <T>
 *            申请类型枚举类型
 * @param <A>
 *            审核者类型
 */
public interface AuditApplymentUnityService<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends UnityService<U, Long>, OwnedUnityService<U, Long, Integer> {

    /**
     * 获取指定审核类型的审核方针
     *
     * @param type
     *            审核类型
     * @return 审核方针
     */
    AuditPolicy<U, T, A> loadPolicy(T type);

    U findLast(T type, Integer applicantId, Long relatedId, AuditStatus... statuses);

    void transform(SubmitModel<U> submitModel, U unity);

    U add(T type, AuditApplymentSubmitModel<U> model, boolean submitted) throws HandleableException;

    QueryResult<U> findAuditing(T type, A auditor, int pageSize, int pageNo);

    Map<T, Integer> countAuditingGroupByType(A auditor);

    QueryResult<U> find(T type, AuditApplymentUnityQueryParameter parameter);

    int count(T type, int relatedId, AuditStatus... status);

    List<U> find(T type, int relatedId, AuditStatus... status);

    List<U> findPassed(T type, int relatedId, Date beforeApplyTime, Date afterApplyTime);

    void updateStatus(long id, AuditStatus status);
}
