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
import org.truenewx.verify.data.dao.VerifyEntityDao;
import org.truenewx.verify.data.model.VerifyEntity;

/**
 * 验证器实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public class VerifierImpl<E extends VerifyEntity<T>, T extends Enum<T>> extends AbstractService<E>
        implements Verifier<E, T>, VerifyPolicyRegistrar<E, T> {

    private VerifyEntityDao<E, T> verifyEntityDao;
    private Map<T, VerifyPolicy<E, T>> policies = new HashMap<>();

    public void setVerifyEntityDao(final VerifyEntityDao<E, T> verifyEntityDao) {
        this.verifyEntityDao = verifyEntityDao;
    }

    @Override
    public void register(final VerifyPolicy<E, T> policy) {
        this.policies.put(policy.getVerifyType(), policy);
    }

    @Override
    @WriteTransactional
    public Long send(final T type, final Map<String, Object> content, final Locale locale)
            throws HandleableException {
        final VerifyPolicy<E, T> policy = getPolicy(type);
        policy.validate(content);
        final String code = policy.generateCode(content);
        if (code != null) {
            final E entity = ensureNonnull(null);
            entity.setType(type);
            entity.setContent(content);
            entity.setCode(code);
            entity.setCreateTime(new Date());
            final long expiredTime = entity.getCreateTime().getTime()
                    + policy.getExpiredInterval(content);
            entity.setExpiredTime(new Date(expiredTime));
            this.verifyEntityDao.save(entity);
            policy.send(code, content, locale);
            return entity.getId();
        }
        return null;
    }

    private VerifyPolicy<E, T> getPolicy(final T type) throws BusinessException {
        final VerifyPolicy<E, T> policy = this.policies.get(type);
        if (policy == null) {
            throw new BusinessException(VerifyExceptionCodes.UNSUPPORTED_TYPE, type);
        }
        return policy;
    }

    @Override
    @WriteTransactional
    public void resend(final long id, final Locale locale) throws HandleableException {
        final E entity = this.verifyEntityDao.find(id);
        if (entity != null) {
            final VerifyPolicy<E, T> policy = getPolicy(entity.getType());
            final Map<String, Object> content = entity.getContent();
            if (entity.isExpired()) { // 如已过期则生成新的验证码
                entity.setCode(policy.generateCode(content));
                final long expiredTime = new Date().getTime() + policy.getExpiredInterval(content);
                entity.setExpiredTime(new Date(expiredTime));
                this.verifyEntityDao.save(entity);
            }
            policy.send(entity.getCode(), content, locale);
        }
    }

    @Override
    @ReadTransactional
    public boolean isValid(final long id, final String code) {
        final E entity = this.verifyEntityDao.find(id);
        return isValid(entity, code);
    }

    private boolean isValid(final E entity, final String code) {
        return entity != null && entity.getCode().equals(code) && !entity.isExpired();
    }

    @Override
    @ReadTransactional
    public boolean isValid(final String code) {
        final E entity = this.verifyEntityDao.findByCode(code);
        return isValid(entity, code);
    }

    @Override
    @WriteTransactional
    public E verify(final long id, final String code, final Object context)
            throws HandleableException {
        final E entity = this.verifyEntityDao.find(id);
        verify(entity, code, context);
        return entity;
    }

    private void verify(final E entity, final String code, final Object context)
            throws HandleableException {
        if (entity == null || !entity.getCode().equals(code)) {
            throw new BusinessException(VerifyExceptionCodes.WRONG_CODE);
        }
        if (entity.isExpired()) {
            throw new BusinessException(VerifyExceptionCodes.OVERDUE_CODE);
        }
        final VerifyPolicy<E, T> policy = getPolicy(entity.getType());
        final Map<String, Object> content = entity.getContent();
        policy.validate(content);
        if (policy.onVerified(entity, context)) {
            this.verifyEntityDao.delete(entity);
        }
    }

    @Override
    @WriteTransactional
    public E verify(final String code, final Object context) throws HandleableException {
        final E entity = this.verifyEntityDao.findByCode(code);
        verify(entity, code, context);
        return entity;
    }

    @Override
    @WriteTransactional
    public void clean() {
        this.verifyEntityDao.deleteByLatestExpiredTime(new Date());
    }

}
