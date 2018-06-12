package org.truenewx.support.audit.service;

import org.truenewx.support.audit.data.dao.AuditApplymentUnityDao;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核申请实体Dao提供者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuditApplymentUnityDaoSupplier<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>> {

    AuditApplymentUnityDao<U, T, A> getAuditApplymentUnityDao();

}
