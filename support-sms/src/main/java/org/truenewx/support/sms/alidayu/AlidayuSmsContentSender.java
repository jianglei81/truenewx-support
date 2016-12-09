package org.truenewx.support.sms.alidayu;

import java.util.concurrent.Executor;

import org.truenewx.support.sms.send.SmsContentSender;
import org.truenewx.support.sms.send.SmsSendCallback;
import org.truenewx.support.sms.send.SmsSendResult;

/**
 * 阿里大鱼的短信内容发送器
 *
 * @author jianglei
 *
 */
public class AlidayuSmsContentSender implements SmsContentSender {
    private Executor executor;

    private String url;
    private String appKey;
    private String freeSignName;
    private String templateCode;

    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    /**
     * @param url
     *            请求地址
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @param appKey
     *            app_key
     */
    public void setAppKey(final String appKey) {
        this.appKey = appKey;
    }

    /**
     * @param freeSignName
     *            sms_free_sign_name
     */
    public void setFreeSignName(final String freeSignName) {
        this.freeSignName = freeSignName;
    }

    /**
     * @param templateCode
     *            sms_template_code
     */
    public void setTemplateCode(final String templateCode) {
        this.templateCode = templateCode;
    }

    @Override
    public SmsSendResult send(final String content, final int maxCount,
            final String... mobilePhones) {
        return send(content, mobilePhones);
    }

    @Override
    public void send(final String content, final int maxCount, final String[] mobilePhones,
            final SmsSendCallback callback) {
        this.executor.execute(new SendCommand(content, mobilePhones, callback));
    }

    private SmsSendResult send(final String content, final String... mobilePhones) {
        // TODO content此时已经为json格式，将其作为sms_param发送。记得要签名
        return null;
    }

    private class SendCommand implements Runnable {
        private String content;
        private String[] mobilePhones;
        private SmsSendCallback callback;

        public SendCommand(final String content, final String[] mobilePhones,
                final SmsSendCallback callback) {
            this.content = content;
            this.mobilePhones = mobilePhones;
            this.callback = callback;
        }

        @Override
        public void run() {
            final SmsSendResult result = send(this.content, this.mobilePhones);
            this.callback.onSmsSent(result);
        }
    }

}
