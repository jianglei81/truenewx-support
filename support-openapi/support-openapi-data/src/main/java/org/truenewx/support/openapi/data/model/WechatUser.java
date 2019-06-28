package org.truenewx.support.openapi.data.model;

import org.truenewx.data.model.Entity;

/**
 * 微信用户标识
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class WechatUser implements Entity {

    private String openId;
    private String unionId;
    private String accessToken;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
