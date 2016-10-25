package org.truenewx.support.audit.service;

import org.truenewx.support.audit.data.model.AuditLogUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核日志实体创建器
 *
 * @author jianglei
 * @version 1.0.0 2014年11月26日
 * @since JDK 1.8
 */
public interface AuditLogEntityCreator<T extends Enum<T>, A extends Auditor<T>> {

    /**
     * 创建新的审核日志实体
     *
     * @return 新的审核日志实体
     */
    AuditLogUnity<T, A> newLogEntity();

}
