package org.truenewx.support.unstructured.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.model.UserIdentity;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.support.unstructured.core.model.UnstructuredAccessToken;
import org.truenewx.support.unstructured.core.model.UnstructuredInnerUrl;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;
import org.truenewx.support.unstructured.core.model.UnstructuredWriteToken;

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
    private UnstructuredAccessor accessor;

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

        this.accessor = SpringUtil.getFirstBeanByClass(context, UnstructuredAccessor.class);
    }

    @Override
    public UnstructuredWriteToken authorizePrivateWrite(final T authorizeType, final U user) {
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);

            String path = policy.getPath(user, null);
            if (path != null) {
                path = authorizer.standardizePath(path);
                final String userKey = user == null ? null : user.toString();
                final String bucket = policy.getBucket();
                final UnstructuredAccessToken accessToken = authorizer
                        .authorizePrivateWrite(userKey, bucket, path);
                if (accessToken != null) {
                    final UnstructuredWriteToken writeToken = new UnstructuredWriteToken();
                    writeToken.setAccessId(accessToken.getAccessId());
                    writeToken.setAccessSecret(accessToken.getAccessSecret());
                    writeToken.setTempToken(accessToken.getTempToken());
                    writeToken.setExpiredTime(accessToken.getExpiredTime());
                    writeToken.setProvider(provider);
                    writeToken.setHost(authorizer.getHost());
                    writeToken.setBucket(bucket);
                    writeToken.setPath(path);
                    writeToken.setPublicReadable(policy.isPublicReadable());
                    writeToken.setRegion(authorizer.getRegion());
                    return writeToken;
                }
            }
        }
        return null;
    }

    @Override
    public void authorizePublicRead(final T authorizeType, final U user, final String filename) {
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
        if (policy != null) {
            final UnstructuredProvider provider = policy.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
            if (authorizer != null) {
                String path = policy.getPath(user, filename);
                if (path != null) {
                    path = authorizer.standardizePath(path);
                    final String bucket = policy.getBucket();
                    authorizer.authorizePublicRead(bucket, path);
                }
            }
        }
    }

    @Override
    public String getOuterUrl(final T authorizeType, final U user, final String innerUrl,
            final String protocol) {
        final UnstructuredInnerUrl url = new UnstructuredInnerUrl(innerUrl);
        if (url.isValid()) {
            String path = url.getPath();
            final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
            if (policy.isReadable(user, path)) { // 指定用户必须具有该授权类型下的资源的读取授权
                final String userKey = user == null ? null : user.toString();
                final UnstructuredProvider provider = url.getProvider(); // 使用内部协议确定的提供商而不是方针下现有的提供商，以免方针的历史提供商有变化
                final String bucket = url.getBucket();
                final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
                path = authorizer.standardizePath(path);
                final String outerUrl = authorizer.getReadHttpUrl(userKey, bucket, path);
                if (outerUrl != null) {
                    if (StringUtils.isBlank(protocol)) {
                        return outerUrl.replace("http://", "//");
                    } else if ("https".equalsIgnoreCase(protocol)) {
                        return outerUrl.replace("http://", "https://");
                    } else {
                        return outerUrl;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String write(final T authorizeType, final U user, final String filename,
            final InputStream in) throws BusinessException, IOException {
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
        final UnstructuredProvider provider = policy.getProvider();
        if (provider == UnstructuredProvider.OWN) { // 自有的提供商才可调用本方法进行写操作
            final String path = policy.getPath(user, filename);
            if (path == null || !policy.isWritable(user, path)) {
                throw new BusinessException(UnstructuredExceptionCodes.NO_WRITE_PERMISSION);
            }

            final String bucket = policy.getBucket();
            this.accessor.write(bucket, path, in);
            final UnstructuredInnerUrl innerUrl = new UnstructuredInnerUrl(provider, bucket, path);
            return innerUrl.toString();
        }
        return null;
    }

    @Override
    public void read(final U user, final String bucket, final String path, final OutputStream out)
            throws BusinessException, IOException {
        // 遍历方针，找到匹配的方针
        for (final UnstructuredAuthorizePolicy<T, U> policy : this.policies.values()) {
            if (policy.getProvider() == UnstructuredProvider.OWN
                    && policy.getBucket().equals(bucket) && policy.isReadable(user, path)) {
                this.accessor.read(bucket, path, out);
                return;
            }
        }
        // 如果没有找到匹配的方针，则说明没有读权限
        throw new BusinessException(UnstructuredExceptionCodes.NO_READ_PERMISSION);
    }

}
