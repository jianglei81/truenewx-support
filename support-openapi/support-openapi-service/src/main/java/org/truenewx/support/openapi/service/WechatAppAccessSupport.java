package org.truenewx.support.openapi.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.HttpClientUtil;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.support.openapi.data.model.WechatUser;

/**
 * 微信应用访问支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatAppAccessSupport {

    protected static final String HOST = "https://api.weixin.qq.com";

    protected Map<String, Object> get(String url, Map<String, Object> params) {
        try {
            Binate<Integer, String> response = HttpClientUtil.requestByGet(HOST + url, params);
            if (response != null) {
                String body = response.getRight();
                return JsonUtil.json2Map(body);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return new HashMap<>();
    }

    protected Map<String, Object> post(String url, Map<String, Object> params) {
        try {
            Binate<Integer, String> response = HttpClientUtil.requestByPost(HOST + url, params);
            if (response != null) {
                String body = response.getRight();
                return JsonUtil.json2Map(body);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * @return 应用id
     */
    protected abstract String getAppId();

    /**
     * @return 访问秘钥
     */
    protected abstract String getSecret();

    public abstract WechatUser getUser(String loginCode);

}