package org.truenewx.verify.data.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.data.model.unity.AbstractUnity;

/**
 * 验证信息实体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public class VerifyEntity<T extends Enum<T>> extends AbstractUnity<Long> {
    private T type;
    private String code;
    private Map<String, Object> content;
    private Date createTime;
    private Date expiredTime;

    public T getType() {
        return this.type;
    }

    public void setType(final T type) {
        this.type = type;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public Map<String, Object> getContent() {
        return this.content;
    }

    public void setContent(final Map<String, Object> content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpiredTime() {
        return this.expiredTime;
    }

    public void setExpiredTime(final Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    /**
     * 获取内容值
     *
     * @param name
     *            内容字段名
     * @return 内容值
     */
    @SuppressWarnings("unchecked")
    public <V> V getContentValue(final String name) {
        if (this.content != null) {
            return (V) this.content.get(name);
        }
        return null;
    }

    /**
     * 设置内容值
     *
     * @param name
     *            内容字段名
     * @param value
     *            内容值
     */
    public void setContentValue(final String name, final Object value) {
        if (value != null) {
            if (this.content == null) {
                this.content = new HashMap<>();
            }
            this.content.put(name, value);
        } else if (this.content != null) {
            this.content.remove(name);
        }
    }

    /**
     * 判断是否已过期
     *
     * @return 是否已过期
     */
    public boolean isExpired() {
        return this.expiredTime != null && this.expiredTime.getTime() <= System.currentTimeMillis();
    }
}
