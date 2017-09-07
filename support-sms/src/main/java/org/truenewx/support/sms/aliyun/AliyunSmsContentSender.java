package org.truenewx.support.sms.aliyun;

import java.util.Date;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.support.sms.SmsModel;
import org.truenewx.support.sms.send.SmsContentSender;
import org.truenewx.support.sms.send.SmsSendCallback;
import org.truenewx.support.sms.send.SmsSendResult;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsRequest;

/**
 * 阿里云的短信内容发送器
 *
 * @author jianglei
 *
 */
public class AliyunSmsContentSender implements SmsContentSender {
    private Executor executor;
    private String regionId;
    private String accessKey;
    private String accessSecret;
    private String freeSignName;
    private String templateCode;

    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    /**
     * @param accessKey
     *            访问Key
     */
    public void setAccessKey(final String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * @param accessSecret
     *            访问Secret
     */
    public void setAccessSecret(final String accessSecret) {
        this.accessSecret = accessSecret;
    }

    /**
     * @param freeSignName
     *            短信签名
     */
    public void setFreeSignName(final String freeSignName) {
        this.freeSignName = freeSignName;
    }

    /**
     * @param templateCode
     *            模板编号
     */
    public void setTemplateCode(final String templateCode) {
        this.templateCode = templateCode;
    }

    /**
     * @param regionId
     *            地区编号
     */
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
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
        final SmsModel sms = new SmsModel();
        sms.setMobilePhones(mobilePhones);
        sms.setSendTime(new Date());
        final SmsSendResult result = new SmsSendResult(sms);
        try {
            final IClientProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKey,
                    this.accessSecret);
            DefaultProfile.addEndpoint(this.regionId, this.regionId, "Sms", "sms.aliyuncs.com");
            final IAcsClient client = new DefaultAcsClient(profile);
            final SingleSendSmsRequest request = new SingleSendSmsRequest();
            request.setSignName(this.freeSignName);
            request.setTemplateCode(this.templateCode);
            request.setParamString(content);
            request.setRecNum(StringUtils.join(mobilePhones, Strings.COMMA));
            client.getAcsResponse(request);
        } catch (final ClientException e) {
            result.addFailures(mobilePhones);
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return result;
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
