package org.truenewx.support.unstructured.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;
import org.truenewx.support.unstructured.core.model.UnstructuredStorageUrl;
import org.truenewx.support.unstructured.core.model.UnstructuredUploadLimit;

/**
 * 非结构化存储服务模版实现
 *
 * @author jianglei
 *
 */
public class UnstructuredServiceTemplateImpl<T extends Enum<T>, U>
        implements UnstructuredServiceTemplate<T, U>, ContextInitializedBean {

    private Map<T, UnstructuredAuthorizePolicy<T, U>> policies = new HashMap<>();
    private Map<UnstructuredProvider, UnstructuredAuthorizer> authorizers = new HashMap<>();
    private UnstructuredAccessor accessor;

    @Autowired
    public void setAccessor(final UnstructuredAccessor accessor) {
        this.accessor = accessor;
    }

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
    public UnstructuredUploadLimit getUploadLimit(final T authorizeType, final U user) throws BusinessException {
        return getPolicy(authorizeType).getUploadLimit(user);
    }

    private UnstructuredAuthorizePolicy<T, U> getPolicy(final T authorizeType)
            throws BusinessException {
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.get(authorizeType);
        if (policy == null) {
            throw new BusinessException(UnstructuredExceptionCodes.NO_POLICY_FOR_AUTHORIZE_TYPE,
                    authorizeType.name());
        }
        return policy;
    }

    @Override
    public String write(final T authorizeType, final U user, final String filename,
            final InputStream in) throws BusinessException, IOException {
        final UnstructuredAuthorizePolicy<T, U> policy = getPolicy(authorizeType);
        final UnstructuredProvider provider = policy.getProvider();
        final String path = policy.getPath(user, filename);
        if (path == null || !policy.isWritable(user, path)) {
            throw new BusinessException(UnstructuredExceptionCodes.NO_WRITE_PERMISSION);
        }

        final String bucket = policy.getBucket();
        this.accessor.write(bucket, path, in);
        if (policy.isPublicReadable()) {
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
            authorizer.authorizePublicRead(bucket, path);
        }
        return getStorageUrl(provider, bucket, path);
    }

    protected String getStorageUrl(final UnstructuredProvider provider, final String bucket,
            final String path) {
        return new UnstructuredStorageUrl(provider, bucket, path).toString();
    }

    @Override
    public String getReadUrl(final U user, final String storageUrl) throws BusinessException {
        final UnstructuredStorageUrl url = new UnstructuredStorageUrl(storageUrl);
        if (url.isValid()) {
            final String bucket = url.getBucket();
            String path = url.getPath();
            validateUserRead(user, bucket, path);
            // 使用内部协议确定的提供商而不是方针下现有的提供商，以免方针的历史提供商有变化
            final UnstructuredProvider provider = url.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
            final String userKey = user == null ? null : user.toString();
            path = authorizer.standardizePath(path);
            return authorizer.getReadUrl(userKey, bucket, path);
        }
        return null;
    }

    private void validateUserRead(final U user, final String bucket, final String path)
            throws BusinessException {
        // 存储桶相同，且用户对指定路径具有读权限，则匹配
        // 这要求方针具有唯一的存储桶，或者与其它方针的存储桶相同时，下级存放路径不同
        final UnstructuredAuthorizePolicy<T, U> policy = this.policies.values().stream()
                .filter(p -> p.getBucket().equals(bucket) && p.isReadable(user, path)).findFirst()
                .orElse(null);
        if (policy == null) {
            // 如果没有找到匹配的方针，则说明没有读权限
            throw new BusinessException(UnstructuredExceptionCodes.NO_READ_PERMISSION);
        }
    }

    @Override
    public long getLastModifiedTime(final U user, final String bucket, final String path)
            throws BusinessException {
        validateUserRead(user, bucket, path);
        return this.accessor.getLastModifiedTime(bucket, path);
    }

    @Override
    public void read(final U user, final String bucket, final String path, final OutputStream out)
            throws BusinessException, IOException {
        validateUserRead(user, bucket, path); // 校验读取权限
        this.accessor.read(bucket, path, out);
    }

}
