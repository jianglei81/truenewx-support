package org.truenewx.support.sms.aliyun;

import java.util.Locale;
import java.util.Map;

import org.truenewx.core.util.JsonUtil;
import org.truenewx.support.sms.SmsContentProvider;

/**
 * 阿里云短信内容提供者
 *
 * @author jianglei
 *
 */
public class AliyunSmsContentProvider implements SmsContentProvider {

    private String type;

    public AliyunSmsContentProvider(final String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public int getMaxCount() {
        return 0;
    }

    @Override
    public String getContent(final Map<String, Object> params, final Locale locale) {
        return JsonUtil.toJson(params);
    }

}
