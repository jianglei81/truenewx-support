package org.truenewx.support.openapi.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.core.env.functor.FuncProfile;
import org.truenewx.core.util.EncryptUtil;
import org.truenewx.core.util.MathUtil;
import org.truenewx.support.openapi.core.listener.WeixinOpenApiMessageListener;
import org.truenewx.support.openapi.core.model.WeixinOpenApiEventMessage;
import org.truenewx.support.openapi.core.model.WeixinOpenApiEventType;
import org.truenewx.support.openapi.core.model.WeixinOpenApiMessage;
import org.truenewx.support.openapi.core.model.WeixinOpenApiMessageType;

/**
 * 微信开放接口回调控制器支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WeixinOpenApiCallbackControllerSupport {

    @Autowired
    private WeixinOpenApiMessageListener listener;

    @RequestMapping("/callback")
    @ResponseBody
    public String callback(HttpServletRequest request) {
        if (!checkSignature(request)) {
            return Strings.EMPTY;
        }
        Map<String, String> parameters = getParameters(request);
        WeixinOpenApiMessage message = getMessage(parameters);
        this.listener.onReceived(message);
        return request.getParameter("echostr");
    }

    private boolean checkSignature(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        // 在非生产环境中，签名为空时忽略签名校验
        if (signature == null && !"product".equals(FuncProfile.INSTANCE.apply())) {
            return true;
        }
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String[] array = { getToken(), timestamp, nonce };
        Arrays.sort(array);
        StringBuffer text = new StringBuffer();
        for (String s : array) {
            text.append(s);
        }
        return EncryptUtil.encryptBySha1(text).equals(signature);
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        try {
            SAXReader reader = new SAXReader();
            ServletInputStream in = request.getInputStream();
            if (in.available() > 0) {
                Document doc = reader.read(in);
                Element root = doc.getRootElement();
                @SuppressWarnings("unchecked")
                List<Element> elements = root.elements();
                for (Element e : elements) {
                    parameters.put(e.getName(), e.getText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parameters;
    }

    private WeixinOpenApiMessage getMessage(Map<String, String> parameters) {
        String messageType = parameters.get("MsgType");
        if (StringUtils.isNotBlank(messageType)) {
            messageType = messageType.toUpperCase();
            WeixinOpenApiMessageType type = EnumUtils.getEnum(WeixinOpenApiMessageType.class,
                    messageType);
            if (type != null) {
                WeixinOpenApiMessage message = null;
                switch (type) {
                case EVENT:
                    String event = parameters.get("Event");
                    if (StringUtils.isNotBlank(event)) {
                        WeixinOpenApiEventType eventType = EnumUtils
                                .getEnum(WeixinOpenApiEventType.class, event.toUpperCase());
                        if (eventType != null) {
                            message = new WeixinOpenApiEventMessage(eventType);
                        }
                    }
                    break;
                default:
                    ;
                }
                if (message != null) {
                    message.setId(MathUtil.parseLong(parameters.get("MsgId")));
                    message.setUserOpenId(parameters.get("FromUserName"));
                    long createTime = MathUtil.parseLong(parameters.get("CreateTime"));
                    if (createTime > 0) {
                        message.setCreateTime(Instant.ofEpochMilli(createTime));
                    }
                }
                return message;
            }
        }
        return null;
    }

    protected abstract String getToken();

}
