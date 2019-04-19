package org.truenewx.support.openapi.core.model;

import org.truenewx.data.model.UnitaryEntity;

/**
 * 微信用户信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class WechatUser implements UnitaryEntity<String> {

    private String openId;
    private String unionId;

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

}
