package org.truenewx.support.openapi.core.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.openapi.core.model.WeixinOpenApiMessage;
import org.truenewx.support.openapi.core.model.WeixinOpenApiMessageType;

/**
 * 微信开放接口消息侦听器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class WeixinOpenApiMessageListenerImpl
        implements WeixinOpenApiMessageListener, ContextInitializedBean {

    @Autowired
    private Executor executor;
    private Map<WeixinOpenApiMessageType, List<WeixinOpenApiMessageHandler>> handlerMapping = new HashMap<>();

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, WeixinOpenApiMessageHandler> beans = context
                .getBeansOfType(WeixinOpenApiMessageHandler.class);
        beans.values().forEach(handler -> {
            WeixinOpenApiMessageType[] messageTypes = handler.getMessageTypes();
            for (WeixinOpenApiMessageType messageType : messageTypes) {
                List<WeixinOpenApiMessageHandler> handlers = this.handlerMapping.get(messageType);
                if (handlers == null) {
                    handlers = new ArrayList<>();
                    this.handlerMapping.put(messageType, handlers);
                }
                handlers.add(handler);
            }
        });
    }

    @Override
    public void onReceived(WeixinOpenApiMessage message) {
        if (message != null) {
            List<WeixinOpenApiMessageHandler> handlers = this.handlerMapping.get(message.getType());
            if (handlers != null) {
                handlers.forEach(handler -> {
                    // 以线程形式启动，以快速返回，并确保侦听器互相之间不因异常产生影响
                    this.executor.execute(new Runnable() {

                        @Override
                        public void run() {
                            handler.handleMessage(message);
                        }

                    });
                });
            }
        }
    }

}
