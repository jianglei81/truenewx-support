package org.truenewx.support.sms.http;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.ClientRequestSupport;
import org.truenewx.support.sms.SmsModel;
import org.truenewx.support.sms.send.AbstractSmsContentSender;
import org.truenewx.support.sms.send.SmsSendResult;

/**
 * HTTP方式的短信内容发送器
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class HttpSmsContentSender extends AbstractSmsContentSender {

    private HttpSmsSendStrategy strategy;

    /**
     *
     * @param strategy
     *            短信发送策略
     */
    public void setStrategy(final HttpSmsSendStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    protected SmsSendResult send(final List<String> contents, String... mobilePhones) {
        final SmsModel sms = new SmsModel();
        sms.setContents(contents);
        sms.setMobilePhones(mobilePhones);
        sms.setSendTime(new Date());
        final SmsSendResult result = new SmsSendResult(sms);
        try {
            if (this.strategy.isBatchable()) { // 支持批量
                final Set<String> failures = send(contents, -1, mobilePhones);
                if (failures != null && !failures.isEmpty()) {
                    result.getFailures().addAll(failures);
                }
            } else {
                for (int i = 0; i < contents.size(); i++) {
                    final Set<String> failures = send(contents, i, mobilePhones);
                    if (failures != null && !failures.isEmpty()) {
                        result.getFailures().addAll(failures);
                        // 一次发送失败的手机号码不再发送
                        final String[] failureArray = failures.toArray(new String[failures.size()]);
                        mobilePhones = ArrayUtils.removeElements(mobilePhones, failureArray);
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
            result.addFailures(mobilePhones);
        }
        return result;
    }

    private Set<String> send(final List<String> contents, final int index,
            final String... mobilePhones)
            throws UnsupportedEncodingException, Exception, URISyntaxException {
        final Set<String> mobilePhoneSet = new HashSet<>();
        for (final String mobilePhone : mobilePhones) {
            if (this.strategy.isValid(mobilePhone)) {
                mobilePhoneSet.add(mobilePhone);
            }
        }
        final Map<String, Object> params = this.strategy.getParams(contents, index, mobilePhoneSet);
        final String encoding = this.strategy.getEncoding();
        final ClientRequestSupport request = new ClientRequestSupport();
        request.setMethod(this.strategy.getRequestMethod());
        request.setEncoding(encoding);
        final Binate<Integer, String> binate = request.request(this.strategy.getUrl(), params,
                encoding);
        return this.strategy.getFailures(binate.getLeft(), binate.getRight());
    }

}
