package org.truenewx.support.openapi.service;

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
import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.core.util.JsonUtil;

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

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
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
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding",
                    BouncyCastleProvider.PROVIDER_NAME);
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
            LoggerFactory.getLogger(getClass()).error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public String getUnionId(String openId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", getAccessToken());
        params.put("openid", openId);
        params.put("lang", Locale.getDefault().toString());
        Map<String, Object> result = get("/cgi-bin/user/info", params);
        return (String) result.get("unionid");
    }

    protected synchronized String getAccessToken() {
        long now = System.currentTimeMillis();
        if (this.accessToken == null || this.accessTokenExpiredTimestamp < now) {
            Map<String, Object> params = new HashMap<>();
            params.put("appid", getAppId());
            params.put("secret", getSecret());
            params.put("grant_type", "client_credential");
            Map<String, Object> result = get("/cgi-bin/token", params);
            this.accessToken = (String) result.get("access_token");
            this.accessTokenExpiredTimestamp = now + ACCESS_TOKEN_INTERVAL;
        }
        return this.accessToken;
    }

    /**
     * @return 开发者微信号
     */
    public abstract String getUsername();

}
