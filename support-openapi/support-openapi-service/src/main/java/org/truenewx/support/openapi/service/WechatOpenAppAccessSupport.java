package org.truenewx.support.openapi.service;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.support.openapi.data.model.WechatUser;
import org.truenewx.support.openapi.data.model.WechatUserDetail;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信开放平台（open.weixin.qq.com）应用访问支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatOpenAppAccessSupport extends WechatAppAccessSupport {

    @Override
    public WechatUser getUser(String loginCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", getAppId());
        params.put("secret", getSecret());
        params.put("code", loginCode);
        params.put("grant_type", "authorization_code");
        Map<String, Object> result = get("/sns/oauth2/access_token", params);
        String openId = (String) result.get("openid");
        if (StringUtils.isNotBlank(openId)) { // openId不能为空
            WechatUser user = new WechatUser();
            user.setOpenId(openId);
            user.setUnionId((String) result.get("unionid"));
            user.setAccessToken((String) result.get("access_token"));
            return user;
        }
        return null;
    }

    public WechatUserDetail getUserDetail(String openId, String accessToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("openId", openId);
        params.put("access_token", accessToken);
        Map<String, Object> result = get("/sns/userinfo", params);
        if (openId.equals(result.get("openid"))) {
            WechatUserDetail user = new WechatUserDetail();
            user.setOpenId(openId);
            user.setAccessToken(accessToken);
            user.setUnionId((String) result.get("unionid"));
            user.setHeadImageUrl((String) result.get("headimgurl"));
            user.setNickname((String) result.get("nickname"));
            Integer sex = (Integer) result.get("sex");
            if (sex != null) {
                if (sex == 1) {
                    user.setMale(true);
                } else if (sex == 2) {
                    user.setMale(false);
                }
            }
            user.setCountry((String) result.get("country"));
            user.setProvince((String) result.get("province"));
            user.setCity((String) result.get("city"));
            return user;
        }
        return null;
    }

}
