package org.truenewx.support.openapi.core.access;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.ClientRequestSupport;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.support.openapi.core.model.WechatUser;

/**
 * 微信应用访问支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatAppAccessSupport {

    private static final String HOST = "https://api.weixin.qq.com";
    private static final long ACCESS_TOKEN_INTERVAL = 1000 * 60 * 60; // 有效期1小时
    private String accessToken;
    private Long accessTokenExpiredTimestamp = 0L; // 作为同步锁，定义为Long对象

    private Map<String, Object> request(String url, Map<String, Object> params, boolean getMethod) {
        ClientRequestSupport request = new ClientRequestSupport();
        if (getMethod) {
            request.setMethod("GET");
        }
        try {
            Binate<Integer, String> response = request.request(HOST + url, params);
            String body = response.getRight();
            return JsonUtil.json2Map(body);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            return new HashMap<>();
        }
    }

    public WechatUser getUser(String loginCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", getAppId());
        params.put("secret", getSecret());
        params.put("js_code", loginCode);
        params.put("grant_type", "authorization_code");
        Map<String, Object> result = request("/sns/jscode2session", params, true);
        String openId = (String) result.get("openid");
        if (StringUtils.isNotBlank(openId)) { // openId不能为空
            WechatUser user = new WechatUser();
            user.setOpenId(openId);
            user.setUnionId((String) result.get("unionid"));
            return user;
        }
        return null;
    }

    public String getUnionId(String openId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", getAccessToken());
        params.put("openid", openId);
        params.put("lang", Locale.getDefault().toString());
        Map<String, Object> result = request("/cgi-bin/user/info", params, true);
        return (String) result.get("unionid");
    }

    protected String getAccessToken() {
        long now = System.currentTimeMillis();
        synchronized (this.accessTokenExpiredTimestamp) {
            if (this.accessToken == null || this.accessTokenExpiredTimestamp < now) {
                Map<String, Object> params = new HashMap<>();
                params.put("appid", getAppId());
                params.put("secret", getSecret());
                params.put("grant_type", "client_credential");
                Map<String, Object> result = request("/cgi-bin/token", params, true);
                this.accessToken = (String) result.get("access_token");
                this.accessTokenExpiredTimestamp = now + ACCESS_TOKEN_INTERVAL;
            }
            return this.accessToken;
        }
    }

    /**
     *
     * @return 应用id
     */
    protected abstract String getAppId();

    /**
     *
     * @return 访问秘钥
     */
    protected abstract String getSecret();

    /**
     *
     * @return 开发者微信号
     */
    public abstract String getUsername();

}
