package org.truenewx.support.sms.http.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.support.sms.http.AbstractHttpSmsSendStrategy;

/**
 * 瑞信通的短信发送策略
 *
 * @author liuzhiyi
 * @since JDK 1.8
 */
public class RxtSmsSendStrategy extends AbstractHttpSmsSendStrategy {

    private String contentSuffix;

    public RxtSmsSendStrategy() {
        setEncoding("gb2312");
    }

    public void setContentSuffix(final String contentSuffix) {
        this.contentSuffix = contentSuffix;
    }

    @Override
    public boolean isBatchable() {
        return false;
    }

    @Override
    public boolean isValid(final String mobilePhone) {
        return true;
    }

    @Override
    public Map<String, Object> getParams(final List<String> contents, final int index,
            final Set<String> mobilePhones) {
        Map<String, Object> params;
        if (this.defaultParams == null) {
            params = new HashMap<>();
        } else {
            params = new HashMap<>(this.defaultParams);
        }

        // smsText
        final StringBuffer contentString = new StringBuffer();
        if (index < 0) {
            for (final String content : contents) {
                /*
                 * try { content = URLEncoder.encode(content, getEncoding()); }
                 * catch (final UnsupportedEncodingException e) { }
                 */
                contentString.append(content);
            }
        } else {
            final String content = contents.get(index);
            /*
             * try { content = URLEncoder.encode(content, getEncoding()); }
             * catch (final UnsupportedEncodingException e) { }
             */
            contentString.append(content);
        }
        if (StringUtils.isNotBlank(this.contentSuffix)) {
            contentString.append(this.contentSuffix);
        }
        params.put("message", contentString.toString());

        // smsMob
        params.put("mobile", StringUtils.join(mobilePhones, Strings.COMMA));
        return params;
    }

    @Override
    public Set<String> getFailures(final int statusCode, final String content) {
        return null;
    }

}
