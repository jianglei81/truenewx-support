package org.truenewx.support.openapi.web.controller;

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
import org.truenewx.support.openapi.core.access.WechatMessageListener;
import org.truenewx.support.openapi.core.model.WechatEventMessage;
import org.truenewx.support.openapi.core.model.WechatEventType;
import org.truenewx.support.openapi.core.model.WechatMessage;
import org.truenewx.support.openapi.core.model.WechatMessageType;
import org.truenewx.support.openapi.core.model.WechatTextMessage;

/**
 * 微信开放接口回调控制器支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class WechatCallbackControllerSupport {

    @Autowired
    private WechatMessageListener listener;

    @RequestMapping("/callback")
    @ResponseBody
    public String callback(HttpServletRequest request) {
        if (checkSignature(request)) {
            Map<String, String> parameters = getParameters(request);
            WechatMessage message = getMessage(parameters);
            if (message == null) { // 无法解析出消息的，为验证访问，直接返回echostr参数
                return request.getParameter("echostr");
            }
            WechatMessage reply = this.listener.onReceived(message);
            if (reply != null) {
                return toXml(reply);
            }
        }
        return Strings.EMPTY;
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

    private WechatMessage getMessage(Map<String, String> parameters) {
        String messageType = parameters.get("MsgType");
        if (StringUtils.isNotBlank(messageType)) {
            messageType = messageType.toUpperCase();
            WechatMessageType type = EnumUtils.getEnum(WechatMessageType.class, messageType);
            if (type != null) {
                WechatMessage message = null;
                switch (type) {
                case EVENT:
                    String event = parameters.get("Event");
                    if (StringUtils.isNotBlank(event)) {
                        WechatEventType eventType = EnumUtils.getEnum(WechatEventType.class,
                                event.toUpperCase());
                        if (eventType != null) {
                            message = new WechatEventMessage(eventType);
                        }
                    }
                    break;
                default:
                    ;
                }
                if (message != null) {
                    message.setId(MathUtil.parseLong(parameters.get("MsgId")));
                    message.setFromUsername(parameters.get("FromUserName"));
                    message.setToUsername(parameters.get("ToUserName"));
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

    private String toXml(WechatMessage message) {
        if (message instanceof WechatTextMessage) {
            WechatTextMessage tm = (WechatTextMessage) message;
            String xml = "<xml>";
            xml += "<ToUserName><![CDATA[" + tm.getToUsername() + "]]></ToUserName>";
            xml += "<FromUserName><![CDATA[" + tm.getFromUsername() + "]]></FromUserName>";
            xml += "<CreateTime>" + tm.getCreateTime().toEpochMilli() + "</CreateTime>";
            xml += "<MsgType><![CDATA[" + tm.getType().name().toLowerCase() + "]]></MsgType>";
            xml += "<Content><![CDATA[" + tm.getContent() + "]]></Content>";
            xml += "</xml>";
            return xml;
        }
        return Strings.EMPTY;
    }

    protected abstract String getToken();

}
