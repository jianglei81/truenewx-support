package org.truenewx.support.unstructured;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.model.UserIdentity;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.unstructured.model.UnstructuredAccess;
import org.truenewx.support.unstructured.model.UnstructuredProvider;
import org.truenewx.support.unstructured.model.UnstructuredWriteToken;

/**
 * 非结构化存储服务模版实现
 *
 * @author jianglei
 *
 */
public class UnstructuredServiceTemplateImpl<T extends Enum<T>, U extends UserIdentity>
        implements UnstructuredServiceTemplate<T, U>, ContextInitializedBean {

    private Map<T, UnstructuredAuthorizePolicy<T, U>> policies = new HashMap<>();
    private Map<UnstructuredProvider, UnstructuredAuthorizer> authorizers = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, UnstructuredAuthorizePolicy> policies = context
                .getBeansOfType(UnstructuredAuthorizePolicy.class);
        for (final UnstructuredAuthorizePolicy<T, U> policy : policies.values()) {
            this.policies.put(policy.getType(), policy);
        }

        final Map<String, UnstructuredAuthorizer> authorizers = context
                .getBeansOfType(UnstructuredAuthorizer.class);
        for (final UnstructuredAuthorizer authorizer : authorizers.values()) {
            this.authorizers.put(authorizer.getProvider(), authorizer);
        }
    }

    @Override
    public UnstructuredWriteToken authorizePrivateWrite(final T authorizeType, final U user) {
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            final String userKey = user.toString();
            final String bucket = policy.getBucket(user);
            String path = policy.getPath(user, null);
            path = authorizer.standardizePath(path);
            final UnstructuredAccess access = authorizer.authorizePrivateWrite(userKey, bucket,
                    path);
            if (access != null) {
                final UnstructuredWriteToken token = new UnstructuredWriteToken();
                token.setAccessId(access.getAccessId());
                token.setAccessSecret(access.getAccessSecret());
                token.setTempToken(access.getTempToken());
                token.setExpiredTime(access.getExpiredTime());
                token.setProvider(provider);
                token.setHost(authorizer.getHost());
                token.setBucket(bucket);
                token.setPath(path);
                token.setPublicReadable(policy.isPublicReadable());
                token.setRegion(authorizer.getRegion());
                return token;
            }
        }
        return null;
    }

    @Override
    public void authorizePublicRead(final T authorizeType, final U userId, final String filename) {
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            final String bucket = policy.getBucket(userId);
            String path = policy.getPath(userId, filename);
            path = authorizer.standardizePath(path);
            authorizer.authorizePublicRead(bucket, path);
        }
    }

    @Override
    public String getOuterUrl(final T authorizeType, final U user, final String innerUrl,
            final String protocol) {
        int index1 = innerUrl.indexOf("://");
        if (index1 > 0) {
            final String innerProtocol = innerUrl.substring(0, index1);
            index1 += 3;
            final int index2 = innerUrl.indexOf(Strings.SLASH, index1);
            if (index2 > 0) {
                final String bucket = innerUrl.substring(index1, index2);
                String path = innerUrl.substring(index2);

                final UnstructuredProvider provider = UnstructuredProvider
                        .valueOf(innerProtocol.toUpperCase());

                final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
                if (policy.isReadable(user, path)) { // 指定用户必须可去读该授权类型下的资源
                    final String userKey = user.toString();
                    final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
                    path = authorizer.standardizePath(path);
                    final String url = authorizer.getReadHttpUrl(userKey, bucket, path);
                    if (url != null) {
                        if (StringUtils.isBlank(protocol)) {
                            return url.replace("http://", "//");
                        } else if ("https".equalsIgnoreCase(protocol)) {
                            return url.replace("http://", "https://");
                        } else {
                            return url;
                        }
                    }
                }
            }
        }
        return null;
    }

}
