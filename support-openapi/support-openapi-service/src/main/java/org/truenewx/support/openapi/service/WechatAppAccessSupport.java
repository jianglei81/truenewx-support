package org.truenewx.support.openapi.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.HttpClientUtil;
import org.truenewx.core.util.JsonUtil;

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

    protected void post(String url, Map<String, Object> params) {
        try {
            Binate<Integer, String> response = HttpClientUtil.requestByPost(HOST + url, params);
            if (response != null) {
                String body = response.getRight();
                Map<String, Object> result = JsonUtil.json2Map(body);
                String errorMessage = (String) result.get("errmsg");
                if (!"ok".equals(errorMessage)) {
                    LoggerFactory.getLogger(getClass()).error(errorMessage);
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }

}