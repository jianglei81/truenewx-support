package org.truenewx.support.unstructured.aliyun;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.support.unstructured.model.UnstructuredAccess;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.ram.model.v20150501.CreateAccessKeyRequest;
import com.aliyuncs.ram.model.v20150501.CreateAccessKeyResponse;
import com.aliyuncs.ram.model.v20150501.CreateAccessKeyResponse.AccessKey;
import com.aliyuncs.ram.model.v20150501.CreateUserRequest;
import com.aliyuncs.ram.model.v20150501.DeleteAccessKeyRequest;
import com.aliyuncs.ram.model.v20150501.ListAccessKeysRequest;
import com.aliyuncs.ram.model.v20150501.ListAccessKeysResponse;

/**
 * 阿里云非结构化访问参数提供者
 *
 * @author jianglei
 *
 */
public class AliyunUnstructuredAccessProvider {

    private AliyunAccount account;
    private String accessKeyBucket;

    public AliyunUnstructuredAccessProvider(final AliyunAccount account,
            final String accessKeyBucket) {
        this.account = account;
        this.accessKeyBucket = accessKeyBucket;
    }

    public UnstructuredAccess getUnstructuredAccess(final String userKey) {
        try {
            final UnstructuredAccess access = readCachedAccess(userKey);
            if (access != null) {
                return access;
            }
        } catch (final ClientException e) {
            if ("EntityNotExist.User".equals(e.getErrCode())) { // 用户不存在，则创建用户
                if (!createUser(userKey)) { // 用户创建失败，则直接返回null
                    return null;
                }
            } else { // 不是用户不存在的错误，则返回null
                e.printStackTrace();
                return null;
            }
        }
        // 此处可确保用户存在但没有密钥，此时创建密钥
        return createAccess(userKey);
    }

    private UnstructuredAccess readCachedAccess(final String userKey) throws ClientException {
        final ListAccessKeysRequest listAccessKeysRequest = new ListAccessKeysRequest();
        listAccessKeysRequest.setUserName(userKey);
        final ListAccessKeysResponse response = this.account.getAcsClient()
                .getAcsResponse(listAccessKeysRequest);
        for (final ListAccessKeysResponse.AccessKey accessKey : response.getAccessKeys()) {
            final String accessKeyId = accessKey.getAccessKeyId();
            final String accessKeySecret = readCachedAccessKeySecret(userKey, accessKeyId);
            if (StringUtils.isNotBlank(accessKeySecret)) { // 可以找到缓存的密钥，则直接返回
                return new UnstructuredAccess(accessKeyId, accessKeySecret);
            }
            // 没有找到缓存的密钥，则删除该密钥对，因为已经无法被动态使用
            final DeleteAccessKeyRequest request = new DeleteAccessKeyRequest();
            request.setUserName(userKey);
            request.setUserAccessKeyId(accessKeyId);
            try {
                this.account.getAcsClient().doAction(request);
            } catch (final ClientException e) { // 删除失败并不影响整体逻辑
                e.printStackTrace();
            }
        }
        return null;
    }

    private String readCachedAccessKeySecret(final String userKey, final String accessKeyId) {
        final Properties props = new Properties();
        final String path = getAccessKeyPath(userKey);
        try {
            final OSSObject object = this.account.getOssClient().getObject(this.accessKeyBucket,
                    path);
            final InputStream in = object.getObjectContent();
            props.load(in);
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final OSSException e) {
            if (!"NoSuchKey".equals(e.getErrorCode())) { // 忽略文件不存在错误
                e.printStackTrace();
            }
        }
        return props.getProperty(accessKeyId);
    }

    private String getAccessKeyPath(final String userKey) {
        return "access-key/" + userKey + ".properties";
    }

    private UnstructuredAccess createAccess(final String userKey) {
        final CreateAccessKeyRequest request = new CreateAccessKeyRequest();
        request.setUserName(userKey);
        try {
            final CreateAccessKeyResponse response = this.account.getAcsClient()
                    .getAcsResponse(request);
            final AccessKey accessKey = response.getAccessKey();
            final UnstructuredAccess access = new UnstructuredAccess(accessKey.getAccessKeyId(),
                    accessKey.getAccessKeySecret());
            // 缓存密钥
            writeCachedAccess(userKey, access);
            return access;
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeCachedAccess(final String userKey, final UnstructuredAccess access) {
        final String path = getAccessKeyPath(userKey);
        final String content = access.getAccessId() + "=" + access.getAccessSecret();
        final InputStream in = new ByteArrayInputStream(content.getBytes());
        this.account.getOssClient().putObject(this.accessKeyBucket, path, in);
        try {
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private boolean createUser(final String userKey) {
        final CreateUserRequest request = new CreateUserRequest();
        request.setUserName(userKey);
        try {
            this.account.getAcsClient().doAction(request);
            return true;
        } catch (final ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
