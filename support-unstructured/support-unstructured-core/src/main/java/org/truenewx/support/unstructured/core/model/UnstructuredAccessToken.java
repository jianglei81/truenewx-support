package org.truenewx.support.unstructured.core.model;

import java.util.Date;

import org.truenewx.core.annotation.Caption;

/**
 * 非结构化存储访问令牌
 *
 * @author jianglei
 *
 */
public class UnstructuredAccessToken {

    @Caption("授权访问id")
    private String accessId;

    @Caption("授权访问密钥")
    private String accessSecret;

    @Caption("临时授权令牌")
    private String tempToken;

    @Caption("临时授权过期时间")
    private Date expiredTime;

    public UnstructuredAccessToken() {
    }

    public UnstructuredAccessToken(final String accessId, final String accessSecret) {
        this.accessId = accessId;
        this.accessSecret = accessSecret;
    }

    /**
     * @return 授权访问id
     */
    public String getAccessId() {
        return this.accessId;
    }

    /**
     * @param accessId
     *            授权访问id
     */
    public void setAccessId(final String accessId) {
        this.accessId = accessId;
    }

    /**
     * @return 授权访问密钥
     */
    public String getAccessSecret() {
        return this.accessSecret;
    }

    /**
     * @param accessSecret
     *            授权访问密钥
     */
    public void setAccessSecret(final String accessSecret) {
        this.accessSecret = accessSecret;
    }

    /**
     * @return 临时授权令牌
     */
    public String getTempToken() {
        return this.tempToken;
    }

    /**
     * @param tempToken
     *            临时授权令牌
     */
    public void setTempToken(final String tempToken) {
        this.tempToken = tempToken;
    }

    /**
     * @return 临时授权过期时间
     */
    public Date getExpiredTime() {
        return this.expiredTime;
    }

    /**
     * @param expiredTime
     *            临时授权过期时间
     */
    public void setExpiredTime(final Date expiredTime) {
        this.expiredTime = expiredTime;
    }

}