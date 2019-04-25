package org.truenewx.support.openapi.core.access;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.HttpRequestMethod;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.HttpClientUtil;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.core.util.MathUtil;
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

    private Map<String, Object> request(String url, Map<String, Object> params,
            HttpRequestMethod method) {
        try {
            Binate<Integer, String> response = HttpClientUtil.request(HOST + url, params, method,
                    Strings.ENCODING_UTF8);
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
        Map<String, Object> result = request("/sns/jscode2session", params, HttpRequestMethod.GET);
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
        Map<String, Object> result = request("/cgi-bin/user/info", params, HttpRequestMethod.GET);
        return (String) result.get("unionid");
    }

    /**
     * 获取不限数量的小程序码图片
     *
     * @param parameters 包含的参数集
     * @param page       打开的页面路径，为空时默认进入首页
     * @param width      图片宽度，默认430，最小280，最大1280，单位：px
     * @param color      主色调颜色值，形如：#rrggbb
     * @param hyaline    背景是否透明
     * @return 小程序图片的输入流
     */
    public InputStream getUnlimitedWxacodeImage(Map<String, Object> parameters, String page,
            Integer width, String color, boolean hyaline) {
        String url = HOST + "/wxa/getwxacodeunlimit?access_token=" + getAccessToken();
        Map<String, Object> params = new HashMap<>();
        params.put("scene", getScene(parameters));
        params.put("is_hyaline", hyaline);
        params.put("auto_color", Boolean.TRUE);
        if (page != null) {
            params.put("page", page);
        }
        if (width != null) {
            params.put("width", width);
        }
        // 仅支持形如#rrggbb的颜色值
        if (color != null && color.startsWith(Strings.WELL) && color.length() == 7) {
            int r = MathUtil.hex2Int(color.substring(1, 3), -1);
            int g = MathUtil.hex2Int(color.substring(3, 5), -1);
            int b = MathUtil.hex2Int(color.substring(5, 7), -1);
            if (r >= 0 && g >= 0 && b >= 0) {
                Map<String, String> colorMap = new HashMap<>();
                colorMap.put("r", String.valueOf(r));
                colorMap.put("g", String.valueOf(g));
                colorMap.put("b", String.valueOf(b));
                // 官方API文档中有错误，参数名称应为以下名称，取值应为字符串类型
                params.put("line_color", colorMap);
                params.put("auto_color", Boolean.FALSE);
            }
        }
        return HttpClientUtil.getImageByPostJson(url, params);
    }

    private String getScene(Map<String, Object> parameters) {
        StringBuffer scene = new StringBuffer();
        // 形如：a=1,b=2
        parameters.forEach((key, value) -> {
            scene.append(Strings.COMMA).append(key).append(Strings.EQUAL).append(value);
        });
        if (scene.length() > 0) {
            scene.deleteCharAt(0); // 去掉首位的逗号
        }
        return scene.toString();
    }

    protected String getAccessToken() {
        long now = System.currentTimeMillis();
        synchronized (this.accessTokenExpiredTimestamp) {
            if (this.accessToken == null || this.accessTokenExpiredTimestamp < now) {
                Map<String, Object> params = new HashMap<>();
                params.put("appid", getAppId());
                params.put("secret", getSecret());
                params.put("grant_type", "client_credential");
                Map<String, Object> result = request("/cgi-bin/token", params,
                        HttpRequestMethod.GET);
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
