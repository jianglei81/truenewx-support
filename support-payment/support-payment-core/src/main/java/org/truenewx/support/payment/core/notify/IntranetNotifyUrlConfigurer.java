package org.truenewx.support.payment.core.notify;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.truenewx.core.util.NetUtil;

/**
 * 内网通知地址配置器
 */
@EnableScheduling
public class IntranetNotifyUrlConfigurer implements NotifyUrlConfigurer {

    private String pattern;
    private String url;

    public IntranetNotifyUrlConfigurer(String pattern) {
        this.pattern = pattern;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5) // 每间隔5分钟执行
    public void refresh() {
        String host = NetUtil.getLocalPublicIp();
        this.url = String.format(this.pattern, host);
    }

    @Override
    public String getNotifyUrl() {
        if (this.url == null) {
            refresh();
        }
        return this.url;
    }
}
