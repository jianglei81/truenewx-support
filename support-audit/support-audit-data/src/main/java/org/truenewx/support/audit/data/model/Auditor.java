package org.truenewx.support.audit.data.model;

import java.util.Map;
import java.util.Set;

/**
 * 审核者
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            申请类型的枚举类型
 */
public interface Auditor<T extends Enum<T>> {

    /**
     * 获取审核级别映射集，key-审核类型，value-审核级别集合
     *
     * @return 审核级别映射集
     */
    Map<T, Set<Byte>> getAuditLevels();

    /**
     * 判断对指定类型的申请是否具有指定级别的审核权限
     *
     * @param type
     *            申请类型
     * @param level
     *            审核级别
     * @return 是否具有审核权限
     */
    boolean isAuditable(T type, byte level);

}
