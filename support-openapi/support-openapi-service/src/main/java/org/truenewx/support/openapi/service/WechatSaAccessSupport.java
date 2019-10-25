package org.truenewx.support.openapi.service;

import java.util.HashMap;
import java.util.Map;

import org.truenewx.support.openapi.data.model.WechatTemplateMessage;

/**
 * 微信服务号访问支持
 *
 * @author jianglei
 */
public abstract class WechatSaAccessSupport extends WechatPublicAppAccessSupport {

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
