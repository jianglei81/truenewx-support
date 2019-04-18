package org.truenewx.support.openapi.core.access;

import java.util.HashMap;
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

    private Map<String, Object> request(String url, Map<String, Object> params, boolean getMethod) {
        ClientRequestSupport request = new ClientRequestSupport();
        if (getMethod) {
            request.setMethod("GET");
        }
        try {
            Binate<Integer, String> response = request.request(url, params);
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
        Map<String, Object> result = request("https://api.weixin.qq.com/sns/jscode2session", params,
                true);
        String unionId = (String) result.get("unionid");
        String openId = (String) result.get("openid");
        if (StringUtils.isNotBlank(openId)) { // openId不能为空
            return new WechatUser(unionId, openId);
        }
        return null;
    }

    protected String getAccessToken() {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", getAppId());
        params.put("secret", getSecret());
        params.put("grant_type", "client_credential");
        Map<String, Object> result = request("https://api.weixin.qq.com/cgi-bin/token", params,
                true);
        return (String) result.get("access_token");
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
