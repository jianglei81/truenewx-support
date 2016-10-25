package org.truenewx.support.sms.http.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.support.sms.http.AbstractHttpSmsSendStrategy;

/**
 * 中国网建的短信发送策略
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class WebChineseSmsSendStrategy extends AbstractHttpSmsSendStrategy {

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
        Map<String, Object> params = this.defaultParams;
        if (params == null) {
            params = new HashMap<>();
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
        params.put("smsText", contentString.toString());

        // smsMob
        params.put("smsMob", StringUtils.join(mobilePhones, Strings.COMMA));
        return params;
    }

    @Override
    public Set<String> getFailures(final int statusCode, final String content) {
        return null;
    }

}
