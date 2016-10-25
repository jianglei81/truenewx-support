package org.truenewx.verify.data.dao;

import java.util.Date;

import org.truenewx.data.orm.dao.support.hibernate.HibernateUnityDaoSupport;
import org.truenewx.verify.data.model.VerifyEntity;

/**
 * 验证信息实体DAO的Hibernate实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public class HibernateVerifyEntityDao<E extends VerifyEntity<T>, T extends Enum<T>>
        extends HibernateUnityDaoSupport<E, Long> implements VerifyEntityDao<E, T> {

    @Override
    public E findByCode(final String code) {
        final String hql = "from " + getEntityName() + " where code=:code order by createTime desc";
        return getHibernateTemplate().first(hql, "code", code);
    }

    @Override
    public int deleteByLatestExpiredTime(final Date latestExpiredTime) {
        final String hql = "delete from " + getEntityName()
                + " where expiredTime<=:latestExpiredTime";
        return getHibernateTemplate().update(hql, "latestExpiredTime", latestExpiredTime);
    }

}
