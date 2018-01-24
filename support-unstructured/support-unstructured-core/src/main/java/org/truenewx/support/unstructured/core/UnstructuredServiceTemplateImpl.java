package org.truenewx.support.unstructured.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.encrypt.Md5Encrypter;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.unstructured.core.model.UnstructuredProvider;
import org.truenewx.support.unstructured.core.model.UnstructuredReadMetadata;
import org.truenewx.support.unstructured.core.model.UnstructuredStorageMetadata;
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
    public UnstructuredUploadLimit getUploadLimit(final T authorizeType, final U user)
            throws BusinessException {
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
            InputStream in) throws BusinessException, IOException {
        final UnstructuredAuthorizePolicy<T, U> policy = getPolicy(authorizeType);
        final String extension = validateExtension(policy, user, filename);
        final UnstructuredProvider provider = policy.getProvider();
        String path;
        if (policy.isMd5AsFilename()) {
            in = new BufferedInputStream(in);
            in.mark(Integer.MAX_VALUE);
            final String md5Code = Md5Encrypter.encrypt32(in);
            in.reset();
            path = policy.getPath(user, md5Code + extension);
        } else {
            path = policy.getPath(user, filename);
        }
        if (path == null) {
            throw new BusinessException(UnstructuredExceptionCodes.NO_WRITE_PERMISSION);
        }
        path = standardizePath(path);
        if (!policy.isWritable(user, path)) {
            throw new BusinessException(UnstructuredExceptionCodes.NO_WRITE_PERMISSION);
        }

        final String bucket = policy.getBucket();
        this.accessor.write(bucket, path, filename, in);
        if (policy.isPublicReadable()) {
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
            authorizer.authorizePublicRead(bucket, path);
        }
        return getStorageUrl(provider, bucket, path);
    }

    private String validateExtension(final UnstructuredAuthorizePolicy<T, U> policy, final U user,
            final String filename) throws BusinessException {
        String extension = FilenameUtils.getExtension(filename);
        final UnstructuredUploadLimit uploadLimit = policy.getUploadLimit(user);
        final String[] extensions = uploadLimit.getExtensions();
        if (uploadLimit.isRejectedExtension()) { // 拒绝扩展名模式
            if (ArrayUtils.contains(extensions, extension)) {
                throw new BusinessException(UnstructuredExceptionCodes.UNSUPPORTED_EXTENSION,
                        StringUtils.join(extensions, Strings.COMMA), filename);
            }
        } else { // 允许扩展名模式
            if (!ArrayUtils.contains(extensions, extension)) {
                throw new BusinessException(UnstructuredExceptionCodes.ONLY_SUPPORTED_EXTENSION,
                        StringUtils.join(extensions, Strings.COMMA), filename);
            }
        }
        if (extension.length() > 0) {
            extension = Strings.DOT + extension;
        }
        return extension;
    }

    protected String getStorageUrl(final UnstructuredProvider provider, final String bucket,
            final String path) {
        return new UnstructuredStorageUrl(provider, bucket, path).toString();
    }

    /**
     * 使路径格式标准化，不以斜杠开头，也不以斜杠结尾<br/>
     * 所有存储服务提供商均接收该标准的路径，如服务提供商对路径的要求与此不同，则服务提供商的实现类中再做转换
     *
     * @param path
     *            标准化前的路径
     * @return 标准化后的路径
     */
    private String standardizePath(String path) {
        if (path.startsWith(Strings.SLASH)) { // 不能以斜杠开头
            return path.substring(1);
        }
        if (path.endsWith(Strings.SLASH)) { // 不能以斜杠结尾
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Override
    public String getReadUrl(final U user, final String storageUrl) throws BusinessException {
        return getReadUrl(user, new UnstructuredStorageUrl(storageUrl));
    }

    private String getReadUrl(final U user, final UnstructuredStorageUrl url)
            throws BusinessException {
        if (url.isValid()) {
            final String bucket = url.getBucket();
            final String path = standardizePath(url.getPath());
            validateUserRead(user, bucket, path);
            // 使用内部协议确定的提供商而不是方针下现有的提供商，以免方针的历史提供商有变化
            final UnstructuredProvider provider = url.getProvider();
            final UnstructuredAuthorizer authorizer = this.authorizers.get(provider);
            final String userKey = user == null ? null : user.toString();
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
            final String url = Strings.SLASH + bucket + path;
            throw new BusinessException(UnstructuredExceptionCodes.NO_READ_PERMISSION, url);
        }
    }

    @Override
    public UnstructuredReadMetadata getReadMetadata(final U user, final String storageUrl)
            throws BusinessException {
        final UnstructuredStorageUrl url = new UnstructuredStorageUrl(storageUrl);
        final String readUrl = getReadUrl(user, url);
        if (readUrl != null) { // 不为null，则说明存储url有效且用户权限校验通过
            final UnstructuredStorageMetadata storageMetadata = this.accessor
                    .getStorageMetadata(url.getBucket(), url.getPath());
            if (storageMetadata != null) {
                return new UnstructuredReadMetadata(readUrl, storageMetadata);
            }
        }
        return null;
    }

    @Override
    public long getLastModifiedTime(final U user, final String bucket, String path)
            throws BusinessException {
        path = standardizePath(path);
        validateUserRead(user, bucket, path);
        return this.accessor.getLastModifiedTime(bucket, path);
    }

    @Override
    public void read(final U user, final String bucket, String path, final OutputStream out)
            throws BusinessException, IOException {
        path = standardizePath(path);
        validateUserRead(user, bucket, path); // 校验读取权限
        this.accessor.read(bucket, path, out);
    }

}
