package org.truenewx.support.sms.yunpian;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truenewx.support.sms.SmsModel;
import org.truenewx.support.sms.send.SmsContentSender;
import org.truenewx.support.sms.send.SmsSendCallback;
import org.truenewx.support.sms.send.SmsSendResult;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;

/**
 * 云片短信内容发送器
 *
 * @author liubodong
 * @version 1.0.0 2017年12月6日
 * @since JDK 1.8
 */
public class YunPianSmsContentProvider implements SmsContentSender {
    private Executor executor;
    private Map<String, String> signName;
    private String apiKey;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public SmsSendResult send(final String content, final int maxCount, final Locale locale,
            final String... mobilePhones) {
        return this.send(content, locale, mobilePhones);
    }

    private SmsSendResult send(final String content, final Locale locale,
            final String... mobilePhones) {
        final SmsModel sms = new SmsModel();
        sms.setMobilePhones(mobilePhones);
        sms.setSendTime(new Date());
        final SmsSendResult result = new SmsSendResult(sms);
        final YunpianClient clnt = new YunpianClient(this.apiKey).init();
        final Map<String, String> param = clnt.newParam(2);
        final StringBuffer msg = new StringBuffer("【");
        msg.append(this.signName.get(locale.toString()));
        msg.append("】").append(content);
        final List<String> failures = new ArrayList<>();
        try {
            for (final String phone : mobilePhones) {
                param.put(YunpianClient.MOBILE, phone);
                param.put(YunpianClient.TEXT, msg.toString());
                final Result<SmsSingleSend> clntResult = clnt.sms().single_send(param);
                if (clntResult.getCode() != 0) {
                    failures.add(phone);
                }
            }
            if (CollectionUtils.isNotEmpty(failures)) {
                final String failPhones[] = failures.toArray(new String[] {});
                result.addFailures(failPhones);
            }
        } catch (final Exception e) {
            this.logger.error(e.getMessage(), e);
        } finally {
            clnt.close();
        }
        return result;
    }

    @Override
    public void send(final String content, final int maxCount, final Locale locale,
            final String[] mobilePhones, final SmsSendCallback callback) {
        this.executor.execute(new SendCommand(content, locale, mobilePhones, callback));
    }

    /**
     * @return apiKey
     *
     * @author liubodong
     */
    public String getApiKey() {
        return this.apiKey;
    }

    /**
     * @param apiKey
     *            apiKey
     *
     * @author liubodong
     */
    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    private class SendCommand implements Runnable {
        private String content;
        private Locale locale;
        private String[] mobilePhones;
        private SmsSendCallback callback;

        public SendCommand(final String content, final Locale locale, final String[] mobilePhones,
                final SmsSendCallback callback) {
            this.content = content;
            this.locale = locale;
            this.mobilePhones = mobilePhones;
            this.callback = callback;
        }

        @Override
        public void run() {
            final SmsSendResult result = YunPianSmsContentProvider.this.send(this.content,
                    this.locale, this.mobilePhones);
            this.callback.onSmsSent(result);
        }
    }

    /**
     * @param executor
     *            executor
     *
     * @author liubodong
     */
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    public Map<String, String> getSignName() {
        return this.signName;
    }

    public void setSignName(Map<String, String> signName) {
        this.signName = signName;
    }

}
