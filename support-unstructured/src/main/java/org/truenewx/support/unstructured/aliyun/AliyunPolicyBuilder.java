package org.truenewx.support.unstructured.aliyun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.core.encrypt.Md5Encrypter;
import org.truenewx.core.util.JsonUtil;

/**
 * 阿里云授权方针文档构建器
 *
 * @author jianglei
 *
 */
public class AliyunPolicyBuilder {

    private static String[] READ_OBJECT_ACTION_NAMES = { "GetObject", "GetObjectAcl", "ListParts" };

    private static String[] WRITE_OBJECT_ACTION_NAMES = { "PutObject", "PutObjectAcl",
            "DeleteObject", "AbortMultipartUpload" };

    private String accountId;

    public AliyunPolicyBuilder(final String accountId) {
        this.accountId = accountId;
    }

    public String buildName(final String prefix, final String bucket, final String path) {
        return prefix + bucket + Strings.MINUS + Md5Encrypter.encrypt32(path); // 加密路径以确保无特殊字符
    }

    public String buildReadDocument(final String bucket, final String path) {
        return buildDocument(bucket, path, READ_OBJECT_ACTION_NAMES);
    }

    public String buildWriteDocument(final String bucket, final String path) {
        return buildDocument(bucket, path, WRITE_OBJECT_ACTION_NAMES);
    }

    public String buildDocument(final String bucket, final String path,
            final String[] actionNames) {
        final Map<String, Object> policy = buildPolicyMap(bucket, path, actionNames);
        final String document = JsonUtil.map2Json(policy);
        return document;
    }

    private Map<String, Object> buildPolicyMap(final String bucket, final String path,
            final String[] actionNames) {
        final Map<String, Object> policy = new HashMap<>();
        policy.put("Version", "1");

        final List<Map<String, Object>> statements = new ArrayList<>();
        policy.put("Statement", statements);

        final Map<String, Object> statement = new HashMap<>();
        statements.add(statement);

        final List<String> actions = new ArrayList<>();
        statement.put("Action", actions);
        for (final String actionName : actionNames) {
            actions.add(buildAction(actionName));
        }

        final List<String> resources = new ArrayList<>();
        statement.put("Resource", resources);
        resources.add(buildResource(bucket, path));

        statement.put("Effect", "Allow");

        return policy;
    }

    private String buildAction(final String actionName) {
        return "oss:" + actionName;
    }

    private String buildResource(final String bucket, final String path) {
        String resource = "acs:oss:*:" + this.accountId + Strings.COLON + bucket + Strings.SLASH
                + path;
        if (resource.endsWith(Strings.SLASH)) { // 为目录授权则追加*
            resource += Strings.ASTERISK;
        }
        return resource;
    }

}
