package org.truenewx.support.openapi.core.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.openapi.core.model.WechatMessage;
import org.truenewx.support.openapi.core.model.WechatMessageType;

/**
 * 微信开放接口消息侦听器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class WechatMessageListenerImpl implements WechatMessageListener, ContextInitializedBean {

    @Autowired
    private Executor executor;
    private Map<WechatMessageType, List<WechatMessageHandler>> handlerMapping = new HashMap<>();

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, WechatMessageHandler> beans = context
                .getBeansOfType(WechatMessageHandler.class);
        beans.values().forEach(handler -> {
            WechatMessageType messageType = handler.getMessageType();
            List<WechatMessageHandler> handlers = this.handlerMapping.get(messageType);
            if (handlers == null) {
                handlers = new ArrayList<>();
                this.handlerMapping.put(messageType, handlers);
            }
            handlers.add(handler);
            Collections.sort(handlers);
        });
    }

    @Override
    public WechatMessage onReceived(WechatMessage message) {
        if (message != null) {
            List<WechatMessageHandler> handlers = this.handlerMapping.get(message.getType());
            if (handlers != null) {
                for (WechatMessageHandler handler : handlers) {
                    if (handler instanceof WechatMessageSyncHandler) { // 同步处理
                        WechatMessageSyncHandler syncHandler = (WechatMessageSyncHandler) handler;
                        WechatMessage result = syncHandler.handleMessage(message);
                        if (result != null) {
                            return result;
                        }
                    } else if (handler instanceof WechatMessageAsynHandler) { // 异步处理
                        WechatMessageAsynHandler asynHandler = (WechatMessageAsynHandler) handler;
                        this.executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                asynHandler.handleMessage(message);
                            }
                        });
                    }
                }
            }
        }
        return null;
    }

}
