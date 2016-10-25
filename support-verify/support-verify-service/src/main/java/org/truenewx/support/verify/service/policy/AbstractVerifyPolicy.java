package org.truenewx.support.verify.service.policy;

import java.util.Map;

import org.truenewx.support.email.send.EmailSender;
import org.truenewx.support.sms.send.SmsSender;
import org.truenewx.verify.data.model.VerifyEntity;

/**
 * 抽象的校验方针
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            验证类型枚举类型
 */
public abstract class AbstractVerifyPolicy<E extends VerifyEntity<T>, T extends Enum<T>>
        implements VerifyPolicy<E, T> {
    protected long emailExpiredInterval = 7 * 24 * 60 * 60 * 1000; // 默认7天
    protected EmailSender emailSender;
    protected long smsExpiredInterval = 10 * 60 * 1000; // 默认10分钟
    protected SmsSender smsSender;

    public void setEmailExpiredInterval(final long emailExpiredInterval) {
        this.emailExpiredInterval = emailExpiredInterval;
    }

    public void setEmailSender(final EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setSmsExpiredInterval(final long smsExpiredInterval) {
        this.smsExpiredInterval = smsExpiredInterval;
    }

    public void setSmsSender(final SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    @Override
    public long getExpiredInterval(final Map<String, Object> content) {
        if (isEmailMode(content)) {
            return this.emailExpiredInterval;
        }
        if (isSmsMode(content)) {
            return this.smsExpiredInterval;
        }
        return 0;
    }

    protected abstract boolean isSmsMode(Map<String, Object> content);

    protected abstract boolean isEmailMode(Map<String, Object> content);

}
