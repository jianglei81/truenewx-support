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

    public void setDao(VerifyUnityDao<U, T> dao) {
        this.dao = dao;
    }

    @Override
    public void register(VerifyPolicy<U, T> policy) {
        this.policies.put(policy.getVerifyType(), policy);
    }

    @Override
    @WriteTransactional
    public Long send(T type, Map<String, Object> content, Locale locale)
            throws HandleableException {
        VerifyPolicy<U, T> policy = getPolicy(type);
        policy.validate(content);
        String code = policy.generateCode(content);
        if (code != null) {
            U entity = ensureNotNull(null);
            entity.setType(type);
            entity.setContent(content);
            entity.setCode(code);
            entity.setCreateTime(new Date());
            long expiredTime = entity.getCreateTime().getTime()
                    + policy.getExpiredInterval(content);
            entity.setExpiredTime(new Date(expiredTime));
            this.dao.save(entity);
            policy.send(code, content, locale);
            return entity.getId();
        }
        return null;
    }

    private VerifyPolicy<U, T> getPolicy(T type) throws BusinessException {
        VerifyPolicy<U, T> policy = this.policies.get(type);
        if (policy == null) {
            throw new BusinessException(VerifyExceptionCodes.UNSUPPORTED_TYPE, type);
        }
        return policy;
    }

    @Override
    @WriteTransactional
    public void resend(long id, Locale locale) throws HandleableException {
        U entity = this.dao.find(id);
        if (entity == null) { // 一般为已经通过验证后试图再次发送验证码，或者id传参错误（代码错误或恶意传参，此处忽略这种小概率错误）
            throw new BusinessException(VerifyExceptionCodes.VERIFIED);
        }
        VerifyPolicy<U, T> policy = getPolicy(entity.getType());
        Map<String, Object> content = entity.getContent();
        if (entity.isExpired()) { // 如已过期则生成新的验证码
            entity.setCode(policy.generateCode(content));
            long expiredTime = new Date().getTime() + policy.getExpiredInterval(content);
            entity.setExpiredTime(new Date(expiredTime));
            this.dao.save(entity);
        }
        policy.send(entity.getCode(), content, locale);
    }

    @Override
    @ReadTransactional
    public boolean isValid(long id, String code) {
        U entity = this.dao.find(id);
        return isValid(entity, code);
    }

    private boolean isValid(U entity, String code) {
        return entity != null && entity.getCode().equals(code) && !entity.isExpired();
    }

    @Override
    @ReadTransactional
    public boolean isValid(String code) {
        U entity = this.dao.findByCode(code);
        return isValid(entity, code);
    }

    @Override
    @ReadTransactional
    public U validate(long id, String code) throws BusinessException {
        U entity = this.dao.find(id);
        validate(entity, code);
        return entity;
    }

    private void validate(U entity, String code) throws BusinessException {
        if (entity == null || !entity.getCode().equals(code)) {
            throw new BusinessException(VerifyExceptionCodes.WRONG_CODE);
        }
        if (entity.isExpired()) {
            throw new BusinessException(VerifyExceptionCodes.OVERDUE_CODE);
        }
    }

    @Override
    @ReadTransactional
    public U validate(String code) throws BusinessException {
        U entity = this.dao.findByCode(code);
        validate(entity, code);
        return entity;
    }

    @Override
    @WriteTransactional
    public U verify(long id, String code, Object context) throws HandleableException {
        U entity = this.dao.find(id);
        verify(entity, code, context);
        return entity;
    }

    private void verify(U entity, String code, Object context) throws HandleableException {
        validate(entity, code);
        VerifyPolicy<U, T> policy = getPolicy(entity.getType());
        if (policy.onVerified(entity, context)) {
            this.dao.delete(entity);
        }
    }

    @Override
    @WriteTransactional
    public U verify(String code, Object context) throws HandleableException {
        U entity = this.dao.findByCode(code);
        verify(entity, code, context);
        return entity;
    }

    @Override
    @WriteTransactional
    public void clean() {
        this.dao.deleteByLatestExpiredTime(new Date());
    }

}
