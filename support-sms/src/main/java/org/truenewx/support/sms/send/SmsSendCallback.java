package org.truenewx.support.sms.send;

/**
 * 短信发送回调
 *
 * @author jianglei
 * @since JDK 1.7
 */
public interface SmsSendCallback {
    /**
     * 短信发送完成后被调用，通知发送结果
     *
     * @param result
     *            短信发送结果
     */
    void onSmsSent(SmsSendResult result);
}
