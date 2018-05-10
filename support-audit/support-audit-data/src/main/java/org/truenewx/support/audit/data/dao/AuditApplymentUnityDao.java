package org.truenewx.support.audit.data.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.truenewx.data.orm.dao.OwnedUnityDao;
import org.truenewx.data.query.QueryResult;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditState;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;

/**
 * 审核申请单体DAO
 *
 * @author jianglei
 * @version 1.0.0 2014年9月27日
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 * @param <A>
 *            审核者类型
 */
public interface AuditApplymentUnityDao<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends OwnedUnityDao<U, Long, Integer> {

    U findLast(T type, Integer applicantId, Long relatedId, AuditState... auditStatuses);

    QueryResult<U> findByTypeStatusesMap(Map<T, Set<AuditState>> typeStatusesMap, int pageSize,
            int pageNo);

    Map<T, Integer> countGroupByTypeStatusesMap(Map<T, Set<AuditState>> typeStatusesMap);

    QueryResult<U> find(T type, AuditApplymentUnityQueryParameter parameter);

    int count(final T type, final long relatedId, final AuditState... status);

    List<U> find(final T type, final long relatedId, final AuditState... status);

    List<U> find(final T type, final long relatedId, Date beforeApplyTime, Date afterApplyTime,
            AuditState status);
}
