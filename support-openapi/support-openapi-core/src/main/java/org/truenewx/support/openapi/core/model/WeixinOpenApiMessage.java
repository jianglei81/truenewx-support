package org.truenewx.support.openapi.core.model;

import java.time.Instant;

/**
 * 微信开放接口消息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WeixinOpenApiMessage {

    private long id;
    private String userOpenId;
    private Instant createTime;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserOpenId() {
        return this.userOpenId;
    }

    public void setUserOpenId(String userOpenId) {
        this.userOpenId = userOpenId;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public abstract WeixinOpenApiMessageType getType();

}
