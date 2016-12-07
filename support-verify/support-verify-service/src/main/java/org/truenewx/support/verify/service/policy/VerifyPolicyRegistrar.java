package org.truenewx.support.verify.service.policy;

import org.truenewx.verify.data.model.VerifyUnity;

/**
 * 验证方针注册器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public interface VerifyPolicyRegistrar<U extends VerifyUnity<T>, T extends Enum<T>> {
    /**
     * 注册方针
     *
     * @param policy
     *            方针
     */
    void register(VerifyPolicy<U, T> policy);
}
