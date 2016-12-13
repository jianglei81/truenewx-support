package org.truenewx.support.audit.data.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.data.orm.dao.support.OqlUtil;
import org.truenewx.data.orm.dao.support.hibernate.HibernateOwnedUnityDaoSupport;
import org.truenewx.data.query.Comparison;
import org.truenewx.data.query.QueryResult;
import org.truenewx.data.query.QueryResultImpl;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.AuditStatus;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.data.param.AuditApplymentUnityQueryParameter;

/**
 * 审核申请单体DAO Hibernate实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型枚举类型
 * @param <A>
 *            审核者类型
 */
public class HibernateAuditApplymentUnityDao<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends HibernateOwnedUnityDaoSupport<U, Long, Integer>
        implements AuditApplymentUnityDao<U, T, A> {

    @Override
    public U findLast(final T type, final Integer applicantId, final Long relatedId,
            final AuditStatus... auditStatuses) {
        final StringBuffer hql = new StringBuffer();
        hql.append("from ").append(getEntityName()).append(" where type=:type");
        final Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        if (applicantId != null) {
            hql.append(" and applicantId=:applicantId");
            params.put("applicantId", applicantId);
        }
        if (auditStatuses != null && auditStatuses.length > 0) {
            hql.append(" and ")
                    .append(OqlUtil.buildOrConditionString(params, "status", auditStatuses, null));
        }
        if (relatedId != null) {
            hql.append(" and relatedId=:relatedId");
            params.put("relatedId", relatedId);
        }
        hql.append(" order by createTime desc");
        return getHibernateTemplate().first(hql.toString(), params);
    }

    @Override
    public QueryResult<U> findByTypeStatusesMap(final Map<T, Set<AuditStatus>> typeStatusesMap,
            final int pageSize, final int pageNo) {
        if (typeStatusesMap.isEmpty()) { // 映射集不能为空，否则返回空结果
            return new QueryResultImpl<>(new ArrayList<U>(), pageSize, pageNo, 0);
        }

        final StringBuffer hql = new StringBuffer("from ").append(getEntityName())
                .append(" where ");
        final Map<String, Object> params = new HashMap<>();
        appendTypeStatusesMapCondition(hql, params, typeStatusesMap);

        final int total = getHibernateTemplate()
                .count(StringUtils.join("select count(*) ", hql.toString()), params);
        final List<U> list;
        if (total == 0) {
            list = new ArrayList<>();
        } else {
            hql.append(" order by applyTime");
            list = getHibernateTemplate().list(hql.toString(), params, pageSize, pageNo);
        }
        return new QueryResultImpl<>(list, pageSize, pageNo, total);
    }

    private void appendTypeStatusesMapCondition(final StringBuffer hql,
            final Map<String, Object> params, final Map<T, Set<AuditStatus>> typeStatusesMap) {
        int i = 0;
        for (final Entry<T, Set<AuditStatus>> entry : typeStatusesMap.entrySet()) {
            final Set<AuditStatus> statues = entry.getValue();
            if (!statues.isEmpty()) {
                final String typeParamName = "type" + (i++);
                hql.append("(type=:").append(typeParamName).append(" and ");
                params.put(typeParamName, entry.getKey());

                if (statues.size() > 1) {
                    hql.append(Strings.LEFT_BRACKET);
                }
                int j = 0;
                for (final AuditStatus status : statues) {
                    final String statusParamName = StringUtils.join("status", String.valueOf(i),
                            Strings.UNDERLINE, String.valueOf(j++));
                    hql.append("status=:").append(statusParamName).append(" or ");
                    params.put(statusParamName, status);
                }
                hql.delete(hql.length() - 4, hql.length()); // 删除末尾多出的or
                if (statues.size() > 1) {
                    hql.append(Strings.RIGHT_BRACKET);
                }
                hql.append(") or ");
            }
        }
        hql.delete(hql.length() - 4, hql.length()); // 删除末尾多出的or
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<T, Integer> countGroupByTypeStatusesMap(
            final Map<T, Set<AuditStatus>> typeStatusesMap) {
        if (typeStatusesMap.isEmpty()) { // 映射集不能为空，否则返回空结果
            return new HashMap<>();
        }

        final StringBuffer hql = new StringBuffer("select type,count(*) from ")
                .append(getEntityName()).append(" where ");
        final Map<String, Object> params = new HashMap<>();
        appendTypeStatusesMapCondition(hql, params, typeStatusesMap);
        hql.append(" group by type");
        final List<Object[]> list = getHibernateTemplate().list(hql.toString(), params);
        final Map<T, Integer> result = new HashMap<>();
        for (final Object[] array : list) {
            result.put((T) array[0], ((Long) array[1]).intValue());
        }
        return result;
    }

    @Override
    public QueryResult<U> find(final T type, AuditApplymentUnityQueryParameter parameter) {
        final String entityName = getEntityName();
        final StringBuffer hql = new StringBuffer("from ").append(entityName)
                .append(" where type=:type");
        final Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        if (parameter == null) {
            parameter = new AuditApplymentUnityQueryParameter();
        }
        appendCondition(parameter, hql, params);
        parameter.setOrder("lastAuditTime", Boolean.TRUE);
        parameter.setOrder("applyTime", Boolean.TRUE);
        return pagingQuery(entityName, hql, params, parameter);
    }

    protected void appendCondition(final AuditApplymentUnityQueryParameter parameter,
            final StringBuffer hql, final Map<String, Object> params) {
        final AuditStatus[] statuses = parameter.getStatuses();
        if (statuses != null && statuses.length > 0) {
            hql.append(" and ")
                    .append(OqlUtil.buildOrConditionString(params, "status", statuses, null));
        }
        final Set<Integer> applicantIds = parameter.getApplicantIds();
        if (applicantIds != null && !applicantIds.isEmpty()) {
            final String condition = OqlUtil.buildOrConditionString(params, "applicantId",
                    applicantIds, null);
            if (StringUtils.isNotBlank(condition)) {
                hql.append(" and ").append(condition);
            }
        }
        appendContentCondition(parameter.getContentParams(), hql, params);
    }

    protected void appendContentCondition(final Map<String, Object> contentParams,
            final StringBuffer hql, final Map<String, Object> params) {
        if (contentParams != null) {
            for (final Entry<String, Object> entry : contentParams.entrySet()) {
                final String fieldName = entry.getKey();
                final Object fieldParamValue = entry.getValue();
                // 尝试处理参数为集合的情况
                final List<String> paramValues = new ArrayList<>();
                if (fieldParamValue instanceof Object[]) {
                    for (final Object fieldParam : (Object[]) fieldParamValue) {
                        paramValues.add(buildContentParamValue(fieldName, fieldParam));
                    }
                } else if (fieldParamValue instanceof Collection) {
                    for (final Object fieldParam : (Collection<?>) fieldParamValue) {
                        paramValues.add(buildContentParamValue(fieldName, fieldParam));
                    }
                }
                if (paramValues.size() > 0) { // 如果参数为集合，则构建content的OR条件语句
                    hql.append(" and ").append(OqlUtil.buildOrConditionString(params, "content",
                            paramValues, Comparison.LIKE));
                } else { // 如果参数不为集合，则构建简单的content条件语句
                    hql.append(" and content like :content");
                    Object fieldParam = fieldParamValue;
                    if (fieldParamValue instanceof String) { // 查询字段参数为字符串时，支持模糊查询
                        fieldParam = "%" + fieldParamValue + "%";
                    }
                    final String paramValue = buildContentParamValue(fieldName, fieldParam);
                    params.put("content", paramValue);
                }
            }
        }
    }

    private String buildContentParamValue(final String fieldName, final Object fieldParam) {
        // 形如： ,"name":value,，必须确保content以,开头和结尾，以便于查询
        final StringBuffer paramValue = new StringBuffer(",\"").append(fieldName).append("\":")
                .append(JsonUtil.bean2Json(fieldParam));
        return paramValue.toString();
    }

    @Override
    public int count(final T type, final long relatedId, final AuditStatus... auditStatuses) {
        final StringBuffer hql = new StringBuffer();
        hql.append("select count(*) from ").append(getEntityName())
                .append(" where type=:type and relatedId=:relatedId");
        final Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("relatedId", relatedId);
        if (auditStatuses != null && auditStatuses.length > 0) {
            hql.append(" and ")
                    .append(OqlUtil.buildOrConditionString(params, "status", auditStatuses, null));
        }
        return getHibernateTemplate().count(hql.toString(), params);
    }

    @Override
    public List<U> find(final T type, final long relatedId, final AuditStatus... status) {
        final StringBuffer hql = new StringBuffer();
        hql.append("from ").append(getEntityName())
                .append(" where type=:type and relatedId=:relatedId");
        final Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("relatedId", relatedId);
        if (status != null && status.length > 0) {
            hql.append(" and ")
                    .append(OqlUtil.buildOrConditionString(params, "status", status, null));
        }
        return getHibernateTemplate().list(hql.toString(), params);
    }

    @Override
    public List<U> find(final T type, final long relatedId, final Date beforeApplyTime,
            final Date afterApplyTime, final AuditStatus status) {
        final StringBuffer hql = new StringBuffer();
        hql.append("from ").append(getEntityName()).append(
                " where status=:status and type=:type and relatedId=:relatedId and applyTime>:beforeApplyTime and applyTime<:afterApplyTime");
        final Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("type", type);
        params.put("relatedId", relatedId);
        params.put("beforeApplyTime", beforeApplyTime);
        params.put("afterApplyTime", afterApplyTime);
        return getHibernateTemplate().list(hql.toString(), params);
    }

}
