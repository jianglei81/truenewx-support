package org.truenewx.support.openapi.core.model;

/**
 * 微信用户信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class WechatUser {

    private String unionId;
    private String openId;

    public WechatUser(String unionId, String openId) {
        this.unionId = unionId;
        this.openId = openId;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public String getOpenId() {
        return this.openId;
    }

}
