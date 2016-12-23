package org.truenewx.support.unstructured;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.core.util.NetUtil;
import org.truenewx.support.unstructured.model.UnstructuredAccess;
import org.truenewx.support.unstructured.model.UnstructuredProvider;
import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储服务模版实现
 *
 * @author jianglei
 *
 */
public class UnstructuredServiceTemplateImpl<T extends Enum<T>, K extends Serializable>
        implements UnstructuredServiceTemplate<T, K>, ContextInitializedBean {

    private Map<T, UnstructuredAuthorizePolicy<T, K>> policies = new HashMap<>();
    private Map<UnstructuredProvider, UnstructuredAuthorizer> authorizers = new HashMap<>();
    private Map<UnstructuredProvider, UnstructuredUrlBuilder> urlBuilders = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, UnstructuredAuthorizePolicy> policies = context
                .getBeansOfType(UnstructuredAuthorizePolicy.class);
        for (final UnstructuredAuthorizePolicy<T, K> policy : policies.values()) {
            this.policies.put(policy.getType(), policy);
        }

        final Map<String, UnstructuredAuthorizer> authorizers = context
                .getBeansOfType(UnstructuredAuthorizer.class);
        for (final UnstructuredAuthorizer authorizer : authorizers.values()) {
            this.authorizers.put(authorizer.getProvider(), authorizer);
        }

        final Map<String, UnstructuredUrlBuilder> urlBuilders = context
                .getBeansOfType(UnstructuredUrlBuilder.class);
        for (final UnstructuredUrlBuilder urlBuilder : urlBuilders.values()) {
            this.urlBuilders.put(urlBuilder.getProvider(), urlBuilder);
        }
    }

    @Override
    public UnstructuredWriteToken authorizePrivateWrite(final T authorizeType, final K userId) {
        final UnstructuredAuthorizePolicy<T, K> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            final String userKey = policy.getUserKey(userId);
            final String bucket = policy.getBucket(userId);
            String path = policy.getPath(userId, null);
            path = authorizer.standardizePath(path);
            final UnstructuredAccess access = authorizer.authorizePrivateWrite(userKey, bucket,
                    path);
            if (access != null) {
                final UnstructuredWriteToken token = new UnstructuredWriteToken();
                token.setAccessId(access.getAccessId());
                token.setAccessSecret(access.getAccessSecret());
                token.setProvider(provider);
                token.setHost(authorizer.getHost());
                token.setBucket(bucket);
                token.setPath(path);
                token.setUrl(getInnerUrl(provider, bucket, path));
                token.setPublicReadable(policy.isPublicReadable(userId));
                return token;
            }
        }
        return null;
    }

    @Override
    public void authorizePublicRead(final T authorizeType, final K userId, final String filename) {
        final UnstructuredAuthorizePolicy<T, K> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            final String bucket = policy.getBucket(userId);
            String path = policy.getPath(userId, filename);
            path = authorizer.standardizePath(path);
            authorizer.authorizePublicRead(bucket, path);
        }
    }

    private String getInnerUrl(final UnstructuredProvider provider, final String bucket,
            final String path) {
        // 形如：${proivder}://${bucket}/${path}
        final StringBuffer url = new StringBuffer(provider.name().toLowerCase()).append("://")
                .append(bucket);
        if (!path.startsWith(Strings.SLASH)) {
            url.append(Strings.SLASH);
        }
        url.append(path);
        return url.toString();
    }

    @Override
    public String getOuterUrl(final T authorizeType, final K userId, final String innerUrl,
            final String protocol) {
        int index1 = innerUrl.indexOf("://");
        if (index1 > 0) {
            final String innerProtocol = innerUrl.substring(0, index1);
            index1 += 3;
            final int index2 = innerUrl.indexOf(Strings.SLASH, index1);
            if (index2 > 0) {
                final String bucket = innerUrl.substring(index1, index2);
                final String path = innerUrl.substring(index2);

                final UnstructuredProvider provider = UnstructuredProvider
                        .valueOf(innerProtocol.toUpperCase());
                final UnstructuredUrlBuilder urlBuilder = this.urlBuilders.get(provider);
                final String url = urlBuilder.buildUrl(protocol, bucket, path);

                final UnstructuredAuthorizePolicy<T, K> policy = this.policies.get(authorizeType);
                if (policy.isPublicReadable(userId)) {
                    return url;
                }

                // 非公开可读的，则获取临时读取授权
                final String userKey = policy.getUserKey(userId);
                final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
                final Map<String, Object> params = authorizer.authorizeTempRead(userKey, bucket,
                        path);
                return NetUtil.mergeParams(url, params, null);
            }
        }
        return innerUrl; // 默认返回原始URL
    }

}
