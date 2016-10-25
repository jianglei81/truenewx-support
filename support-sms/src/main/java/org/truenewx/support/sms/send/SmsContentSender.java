package org.truenewx.support.sms.send;

/**
 * 短信内容发送器<br/>
 * 仅负责发送提供的短信内容，不管内容如何生成
 *
 * @author jianglei
 * @since JDK 1.7
 */
public interface SmsContentSender {
    /**
     * 同步发送短信
     *
     * @param content
     *            短信内容
     * @param maxCount
     *            内容拆分的最大条数
     * @param mobilePhones
     *            手机号码清单
     * @return 短信发送结果
     */
    SmsSendResult send(String content, int maxCount, String... mobilePhones);

    /**
     * 异步发送短信
     *
     * @param content
     *            短信内容
     * @param maxCount
     *            内容拆分的最大条数
     * @param mobilePhones
     *            手机号码清单
     * @param callback
     *            短信发送回调
     */
    void send(String content, int maxCount, String[] mobilePhones, SmsSendCallback callback);
}
