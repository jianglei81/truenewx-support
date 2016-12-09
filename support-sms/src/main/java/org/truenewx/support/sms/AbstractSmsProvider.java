package org.truenewx.support.sms;

/**
 * 抽象的短信提供者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractSmsProvider implements SmsContentProvider {
    private String type;
    private int maxCount;

    @Override
    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public int getMaxCount() {
        return this.maxCount;
    }

    public void setMaxCount(final int maxCount) {
        this.maxCount = maxCount;
    }

}
