package org.truenewx.support.payment.core.notify;

/**
 * 简单的通知地址配置器
 */
public class SimpleNotifyUrlConfigurer implements NotifyUrlConfigurer {

    private String notifyUrl;

    public SimpleNotifyUrlConfigurer(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String getNotifyUrl() {
        return this.notifyUrl;
    }

}
