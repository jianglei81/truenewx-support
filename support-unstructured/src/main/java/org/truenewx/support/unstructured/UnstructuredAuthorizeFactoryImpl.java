package org.truenewx.support.unstructured;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储授权工厂实现
 *
 * @author jianglei
 *
 */
public class UnstructuredAuthorizeFactoryImpl<T extends Enum<T>, K extends Serializable>
        implements UnstructuredAuthorizeFactory<T, K>, ContextInitializedBean {

    private Map<T, UnstructuredAuthorizePolicy<T, K>> policies = new HashMap<>();
    private UnstructuredAuthorizer authorizer;

    public void setAuthorizer(final UnstructuredAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, UnstructuredAuthorizePolicy> beans = context
                .getBeansOfType(UnstructuredAuthorizePolicy.class);
        for (final UnstructuredAuthorizePolicy<T, K> policy : beans.values()) {
            this.policies.put(policy.getType(), policy);
        }
    }

    @Override
    public UnstructuredWriteToken authorizeWrite(final T authorizeType, final K userId) {
        final UnstructuredAuthorizePolicy<T, K> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final String userKey = policy.getUserKey(userId);
            final String bucket = policy.getBucket(userId);
            final String path = policy.getPath(userId, null);
            final UnstructuredWriteToken token = this.authorizer.authorizeWrite(userKey, bucket, path);
            if (token != null) {
                token.setPublicReadable(policy.isPublicReadable(userId));
            }
            return token;
        }
        return null;
    }

    @Override
    public void authorizePublicRead(final T authorizeType, final K userId, final String filename) {
        final UnstructuredAuthorizePolicy<T, K> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final String bucket = policy.getBucket(userId);
            final String path = policy.getPath(userId, filename);
            this.authorizer.authorizePublicRead(bucket, path);
        }
    }

}
