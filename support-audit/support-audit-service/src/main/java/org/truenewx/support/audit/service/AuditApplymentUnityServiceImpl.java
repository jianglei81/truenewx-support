package org.truenewx.support.audit.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.Assert;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.orm.dao.OwnedUnityDao;
import org.truenewx.data.query.QueryResult;
import org.truenewx.service.unity.AbstractOwnedUnityService;
import org.truenewx.support.audit.data.dao.AuditApplymentUnityDao;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;
import org.truenewx.support.audit.service.model.AuditApplymentSubmitModel;
import org.truenewx.support.audit.service.policy.AuditPolicy;
import org.truenewx.support.audit.service.policy.AuditPolicyRegistrar;

/**
 * 审核申请单体服务逻辑
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
public class AuditApplymentUnityServiceImpl<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AbstractOwnedUnityService<U, Long, Integer>
        implements AuditApplymentUnityService<U, T, A>, AuditPolicyRegistrar<U, T, A> {

    private AuditApplymentUnityDao<U, T, A> dao;
    private Map<T, AuditPolicy<U, T, A>> policies = new HashMap<>();

    public void setDao(final AuditApplymentUnityDao<U, T, A> dao) {
        this.dao = dao;
    }

    @Override
    protected String getNonexistentErorrCode() {
        return AuditExceptionCodes.NONEXISTENT_APPLYMENT;
    }

    @Override
    protected OwnedUnityDao<U, Long, Integer> getDao() {
        return this.dao;
    }

    @Override
    public void addPolicy(final AuditPolicy<U, T, A> policy) {
        this.policies.put(policy.getType(), policy);
    }

    @Override
    public AuditStatus getState(final Long key) {
        final U entity = find(key);
        if (entity != null) {
            return entity.getStatus();
        }
        return null;
    }

    @Override
    public AuditPolicy<U, T, A> loadPolicy(final T type) {
        final AuditPolicy<U, T, A> policy = this.policies.get(type);
        Assert.notNull(policy, "policy must be not null");
        return policy;
    }

    @Override
    public U findLast(final T type, final Integer applicantId, final Long relatedId,
            final AuditStatus... statuses) {
        return this.dao.findLast(type, applicantId, relatedId, statuses);
    }

    @Override
    public void transform(final SubmitModel<U> submitModel, final U unity) {
        if (submitModel instanceof AuditApplymentSubmitModel) {
            final AuditApplymentSubmitModel<U> model = (AuditApplymentSubmitModel<U>) submitModel;
            unity.setApplicantId(model.getApplicantId());
            unity.setContent(model.getContent());
            unity.setReason(model.getReason());
        }
    }

    @Override
    public U add(final T type, final AuditApplymentSubmitModel<U> model, final boolean submitted)
            throws HandleableException {
        loadPolicy(type); // 加载方针以确保type有效

        final U unity = ensureNotNull(null);
        unity.setStatus(submitted ? AuditStatus.PENDING : AuditStatus.UNAPPLIED); // 立即提交则为待审核状态，否则为未提交状态
        transform(model, unity);
        // 转换完成后再设置重要属性，以避免转换方法中错误设置
        unity.setType(type);
        unity.setStatus(submitted ? AuditStatus.PENDING : AuditStatus.UNAPPLIED);
        unity.setCreateTime(new Date());
        if (unity.getStatus() == AuditStatus.PENDING) { // 直接待审核的申请，申请时间即为创建时间
            unity.setApplyTime(unity.getCreateTime());
        }
        unity.setLastAuditTime(Long.MAX_VALUE); // 添加申请时默认最后审核时间为最未来时间
        this.dao.save(unity);
        return unity;
    }

    @Override
    public QueryResult<U> findAuditing(final T type, final A auditor, final int pageSize,
            final int pageNo) {
        final Map<T, Set<AuditStatus>> typeStatusesMap = new HashMap<>();
        final Map<T, Set<Byte>> levelMap = auditor.getAuditLevels();
        if (type != null) {
            putTypeAuditingStatusesToMap(type, levelMap.get(type), typeStatusesMap);
        } else {
            for (final Entry<T, Set<Byte>> entry : levelMap.entrySet()) {
                putTypeAuditingStatusesToMap(entry.getKey(), entry.getValue(), typeStatusesMap);
            }
        }
        return this.dao.findByTypeStatusesMap(typeStatusesMap, pageSize, pageNo);
    }

    private void putTypeAuditingStatusesToMap(final T type, final Set<Byte> levels,
            final Map<T, Set<AuditStatus>> typeStatusesMap) {
        if (levels != null) {
            final Set<AuditStatus> statuses = new HashSet<>();
            for (final byte level : levels) {
                switch (level) {
                case 1:
                    statuses.add(AuditStatus.PENDING);
                    statuses.add(AuditStatus.REJECTED_2);
                    break;
                case 2:
                    statuses.add(AuditStatus.PASSED_1);
                    break;
                }
            }
            typeStatusesMap.put(type, statuses);
        }
    }

    @Override
    public Map<T, Integer> countAuditingGroupByType(final A auditor) {
        final Map<T, Set<AuditStatus>> typeStatusesMap = new HashMap<>();
        final Map<T, Set<Byte>> levelMap = auditor.getAuditLevels();
        for (final Entry<T, Set<Byte>> entry : levelMap.entrySet()) {
            putTypeAuditingStatusesToMap(entry.getKey(), entry.getValue(), typeStatusesMap);
        }
        return this.dao.countGroupByTypeStatusesMap(typeStatusesMap);
    }

    @Override
    public QueryResult<U> find(final T type, final AuditApplymentUnityQueryParameter parameter) {
        return this.dao.find(type, parameter);
    }

    @Override
    public int count(final T type, final int relatedId, final AuditStatus... status) {
        return this.dao.count(type, relatedId, status);
    }

    @Override
    public List<U> find(final T type, final int relatedId, final AuditStatus... status) {
        return this.dao.find(type, relatedId, status);
    }

    @Override
    public void updateStatus(final long id, final AuditStatus status) {
        final U u = this.find(id);
        if (u != null) {
            u.setStatus(status);
            this.dao.save(u);
        }
    }

    @Override
    public List<U> findPassed(final T type, final int relatedId, final Date beforeApplyTime,
            final Date afterApplyTime) {
        return this.dao.find(type, relatedId, beforeApplyTime, afterApplyTime,
                AuditStatus.PASSED_LAST);
    }
}
