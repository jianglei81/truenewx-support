package org.truenewx.support.openapi.service;

import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.truenewx.core.Strings;
import org.truenewx.core.util.HttpClientUtil;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.core.util.MathUtil;
import org.truenewx.support.openapi.data.model.WechatUser;

/**
 * 微信公众平台（mp.weixin.qq.com）应用访问支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatPublicAppAccessSupport extends WechatAppAccessSupport {

    private static final long ACCESS_TOKEN_INTERVAL = 1000 * 60 * 60; // 有效期1小时
    private String accessToken;
    private long accessTokenExpiredTimestamp = 0L;

    public WechatUser getUser(String loginCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", getAppId());
        params.put("secret", getSecret());
        params.put("js_code", loginCode);
        params.put("grant_type", "authorization_code");
        Map<String, Object> result = request("/sns/jscode2session", params);
        String openId = (String) result.get("openid");
        if (StringUtils.isNotBlank(openId)) { // openId不能为空
            WechatUser user = new WechatUser();
            user.setOpenId(openId);
            user.setUnionId((String) result.get("unionid"));
            user.setSessionKey((String) result.get("session_key"));
            return user;
        }
        return null;
    }

    public String decryptUnionId(String encryptedData, String iv, String sessionKey) {
        if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(iv)
                || StringUtils.isBlank(sessionKey)) {
            return null;
        }
        // 被加密的数据
        byte[] dataBytes = Base64.getDecoder().decode(encryptedData);
        // 加密秘钥
        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
        // 偏移量
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        // 如果密钥不足16位就补足
        int base = 16;
        if (sessionKeyBytes.length % base != 0) {
            int groups = sessionKeyBytes.length / base
                    + (sessionKeyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(sessionKeyBytes, 0, temp, 0, sessionKeyBytes.length);
            sessionKeyBytes = temp;
        }
        try {
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(sessionKeyBytes, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivBytes));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultBytes = cipher.doFinal(dataBytes);
            if (resultBytes != null && resultBytes.length > 0) {
                String json = new String(resultBytes, Strings.ENCODING_UTF8);
                Map<String, Object> result = JsonUtil.json2Map(json);
                @SuppressWarnings("unchecked")
                Map<String, Object> watermark = (Map<String, Object>) result.get("watermark");
                if (watermark == null) {
                    return null;
                }
                if (!getAppId().equals(watermark.get("appid"))) {
                    return null;
                }
                return (String) result.get("unionId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUnionId(String openId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", getAccessToken());
        params.put("openid", openId);
        params.put("lang", Locale.getDefault().toString());
        Map<String, Object> result = request("/cgi-bin/user/info", params);
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
        // 为避免出现=，形如：a-1;b-2，所以key和value中都不能带减号和分号
        parameters.forEach((key, value) -> {
            String sValue = value.toString();
            if (!key.contains(Strings.SEMICOLON) && !key.contains(Strings.MINUS)
                    && !sValue.contains(Strings.SEMICOLON) && !sValue.contains(Strings.MINUS)) {
                scene.append(Strings.SEMICOLON).append(key).append(Strings.MINUS).append(sValue);
            }
        });
        if (scene.length() > 0) {
            scene.deleteCharAt(0); // 去掉首位的分号
        }
        return scene.toString();
    }

    protected synchronized String getAccessToken() {
        long now = System.currentTimeMillis();
        if (this.accessToken == null || this.accessTokenExpiredTimestamp < now) {
            Map<String, Object> params = new HashMap<>();
            params.put("appid", getAppId());
            params.put("secret", getSecret());
            params.put("grant_type", "client_credential");
            Map<String, Object> result = request("/cgi-bin/token", params);
            this.accessToken = (String) result.get("access_token");
            this.accessTokenExpiredTimestamp = now + ACCESS_TOKEN_INTERVAL;
        }
        return this.accessToken;
    }

    /**
     * @return 应用id
     */
    protected abstract String getAppId();

    /**
     * @return 访问秘钥
     */
    protected abstract String getSecret();

    /**
     * @return 开发者微信号
     */
    public abstract String getUsername();

}
