package org.truenewx.support.verify.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.transaction.annotation.ReadTransactional;
import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.service.AbstractService;
import org.truenewx.support.verify.service.policy.VerifyPolicy;
import org.truenewx.support.verify.service.policy.VerifyPolicyRegistrar;
import org.truenewx.verify.data.dao.VerifyUnityDao;
import org.truenewx.verify.data.model.VerifyUnity;

/**
 * 验证器实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public class VerifierImpl<U extends VerifyUnity<T>, T extends Enum<T>> extends AbstractService<U>
        implements Verifier<U, T>, VerifyPolicyRegistrar<U, T> {

    private VerifyUnityDao<U, T> dao;
    private Map<T, VerifyPolicy<U, T>> policies = new HashMap<>();

    public void setDao(final VerifyUnityDao<U, T> dao) {
        this.dao = dao;
    }

    @Override
    public void register(final VerifyPolicy<U, T> policy) {
        this.policies.put(policy.getVerifyType(), policy);
    }

    @Override
    @WriteTransactional
    public Long send(final T type, final Map<String, Object> content, final Locale locale)
            throws HandleableException {
        final VerifyPolicy<U, T> policy = getPolicy(type);
        policy.validate(content);
        final String code = policy.generateCode(content);
        if (code != null) {
            final U entity = ensureNotNull(null);
            entity.setType(type);
            entity.setContent(content);
            entity.setCode(code);
            entity.setCreateTime(new Date());
            final long expiredTime = entity.getCreateTime().getTime()
                    + policy.getExpiredInterval(content);
            entity.setExpiredTime(new Date(expiredTime));
            this.dao.save(entity);
            policy.send(code, content, locale);
            return entity.getId();
        }
        return null;
    }

    private VerifyPolicy<U, T> getPolicy(final T type) throws BusinessException {
        final VerifyPolicy<U, T> policy = this.policies.get(type);
        if (policy == null) {
            throw new BusinessException(VerifyExceptionCodes.UNSUPPORTED_TYPE, type);
        }
        return policy;
    }

    @Override
    @WriteTransactional
    public void resend(final long id, final Locale locale) throws HandleableException {
        final U entity = this.dao.find(id);
        if (entity == null) { // 一般为已经通过验证后试图再次发送验证码，或者id传参错误（代码错误或恶意传参，此处忽略这种小概率错误）
            throw new BusinessException(VerifyExceptionCodes.VERIFIED);
        }
        final VerifyPolicy<U, T> policy = getPolicy(entity.getType());
        final Map<String, Object> content = entity.getContent();
        if (entity.isExpired()) { // 如已过期则生成新的验证码
            entity.setCode(policy.generateCode(content));
            final long expiredTime = new Date().getTime() + policy.getExpiredInterval(content);
            entity.setExpiredTime(new Date(expiredTime));
            this.dao.save(entity);
        }
        policy.send(entity.getCode(), content, locale);
    }

    @Override
    @ReadTransactional
    public boolean isValid(final long id, final String code) {
        final U entity = this.dao.find(id);
        return isValid(entity, code);
    }

    private boolean isValid(final U entity, final String code) {
        return entity != null && entity.getCode().equals(code) && !entity.isExpired();
    }

    @Override
    @ReadTransactional
    public boolean isValid(final String code) {
        final U entity = this.dao.findByCode(code);
        return isValid(entity, code);
    }

    @Override
    @WriteTransactional
    public U verify(final long id, final String code, final Object context)
            throws HandleableException {
        final U entity = this.dao.find(id);
        verify(entity, code, context);
        return entity;
    }

    private void verify(final U entity, final String code, final Object context)
            throws HandleableException {
        if (entity == null || !entity.getCode().equals(code)) {
            throw new BusinessException(VerifyExceptionCodes.WRONG_CODE);
        }
        if (entity.isExpired()) {
            throw new BusinessException(VerifyExceptionCodes.OVERDUE_CODE);
        }
        final VerifyPolicy<U, T> policy = getPolicy(entity.getType());
        if (policy.onVerified(entity, context)) {
            this.dao.delete(entity);
        }
    }

    @Override
    @WriteTransactional
    public U verify(final String code, final Object context) throws HandleableException {
        final U entity = this.dao.findByCode(code);
        verify(entity, code, context);
        return entity;
    }

    @Override
    @WriteTransactional
    public void clean() {
        this.dao.deleteByLatestExpiredTime(new Date());
    }

}
