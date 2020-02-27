package org.truenewx.support.openapi.service;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.support.openapi.data.model.WechatTemplateMessage;
import org.truenewx.support.openapi.data.model.WechatUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信服务号访问支持
 *
 * @author jianglei
 */
public abstract class WechatSaAccessSupport extends WechatPublicAppAccessSupport {

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
            user.setAccessToken((String) result.get("access_token"));
            return user;
        }
        return null;
    }

    /**
     * 发送公众号模板消息
     *
     * @param openId     接收用户openId
     * @param templateId 消息模板id
     * @param message    模板消息内容
     * @param mpAppId    关联小程序AppId，为空时不关联小程序
     * @param mpPagePath 关联小程序打开的页面路径，为空时不关联小程序
     */
    public void sendTemplateMessage(String openId, String templateId, WechatTemplateMessage message,
            String mpAppId, String mpPagePath) {
        Map<String, Object> params = new HashMap<>();
        params.put("touser", openId);
        params.put("template_id", templateId);
        if (mpAppId != null && mpPagePath != null) {
            Map<String, String> mpParams = new HashMap<>();
            mpParams.put("appid", mpAppId);
            mpParams.put("pagepath", mpPagePath);
            params.put("miniprogram", mpParams);
        }
        params.put("data", message.toMap());
        String url = "/cgi-bin/message/template/send?access_token=" + getAccessToken();
        post(url, params);
    }

}
