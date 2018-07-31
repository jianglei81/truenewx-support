package org.truenewx.support.log.web.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.StringUtil;
import org.truenewx.support.log.data.model.Action;
import org.truenewx.support.log.service.ActionLogWriter;
import org.truenewx.support.log.web.annotation.LogExcluded;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.menu.model.ActableMenuItem;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.rpc.server.RpcInvokeInterceptor;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.util.WebUtil;

/**
 * 操作日志拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractActionLogInterceptor<K extends Serializable>
        implements HandlerInterceptor, RpcInvokeInterceptor {

    @Autowired
    private Executor executor;
    @Autowired
    private ActionLogWriter<K> writer;

    private Collection<HttpLink> excludedUrlPatterns;
    private Collection<RpcPort> excludedRpcPatterns;

    public void setExcludedUrlPatterns(String[] excludedUrlPatterns) {
        if (excludedUrlPatterns != null) {
            this.excludedUrlPatterns = new ArrayList<>();
            for (String pattern : excludedUrlPatterns) {
                String[] pair = pattern.split(Strings.SEMICOLON);
                String url = pair[0];
                HttpMethod method = null;
                if (pair.length > 1) {
                    method = EnumUtils.getEnum(HttpMethod.class, pair[1]);
                }
                this.excludedUrlPatterns.add(new HttpLink(url, method));
            }
        }
    }

    public void setExcludedRpcPatterns(String[] excludedRpcPatterns) {
        if (excludedRpcPatterns != null) {
            this.excludedRpcPatterns = new ArrayList<>();
            for (String pattern : excludedRpcPatterns) {
                String[] pair = pattern.split("\\.");
                String beanId = pair[0];
                String methodName = Strings.ASTERISK;
                Integer argCount = null;
                if (pair.length > 1) {
                    int index = pair[1].indexOf(Strings.LEFT_BRACKET);
                    if (index < 0) {
                        methodName = pair[1];
                    } else {
                        methodName = pair[1].substring(0, index);
                        String argString = pair[1].substring(index + 1,
                                pair[1].indexOf(Strings.RIGHT_BRACKET));
                        argCount = argString.split(Strings.COMMA).length;
                    }
                }
                RpcPort rpcPort = argCount == null ? new RpcPort(beanId, methodName)
                        : new RpcPort(beanId, methodName, argCount);
                this.excludedRpcPatterns.add(rpcPort);
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        return true;
    }

    protected boolean matches(String url, HttpMethod method) {
        if (url.startsWith("/rpc/")) { // 忽略所有RPC请求
            return false;
        }
        if (this.excludedUrlPatterns != null) {
            for (HttpLink pattern : this.excludedUrlPatterns) {
                if (pattern.matches(url, method)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 请求失败或为include请求则忽略当前拦截器
        if (response.getStatus() != HttpServletResponse.SC_OK
                || WebUtils.isIncludeRequest(request)) {
            return;
        }
        K userId = getUserId();
        if (userId != null) { // 已登录的才需要记录日志
            String url = WebUtil.getRelativeRequestUrl(request);
            HttpMethod method = EnumUtils.getEnum(HttpMethod.class, request.getMethod());
            if (matches(url, method) && handler instanceof HandlerMethod) { // URL匹配才进行校验
                HandlerMethod hm = (HandlerMethod) handler;
                LogExcluded logExcluded = hm.getMethodAnnotation(LogExcluded.class);
                if (logExcluded != null && logExcluded.value()) { // 整个方法都被排除
                    return;
                }
                // 在创建线程提交执行之前获取菜单和请求参数，以免线程执行环境无法获取
                Menu menu = getMenu();
                String caption = getUrlActionCaption(menu, url, method, hm);
                if (StringUtils.isNotBlank(caption)) {
                    String beanId = getControllerBeanId(hm.getBeanType());
                    Map<String, Object> params;
                    if (logExcluded != null) {
                        params = WebUtil.getRequestParameterMap(request, logExcluded.excluded());
                    } else {
                        params = WebUtil.getRequestParameterMap(request);
                    }
                    this.executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Action action = new Action(beanId, url, method.name(), params);
                            action.setCaption(caption);
                            AbstractActionLogInterceptor.this.writer.add(userId, action);
                        }
                    });
                }
            }
        }
    }

    private String getControllerBeanId(Class<?> controllerClass) {
        String beanId = null;
        Controller controller = controllerClass.getAnnotation(Controller.class);
        if (controller != null) {
            beanId = controller.value();
        } else {
            RpcController rpcController = controllerClass.getAnnotation(RpcController.class);
            if (rpcController != null) {
                beanId = rpcController.value();
            }
        }
        if (StringUtils.isEmpty(beanId)) {
            beanId = StringUtil.firstToLowerCase(controllerClass.getSimpleName());
        }
        return beanId;
    }

    private String getUrlActionCaption(Menu menu, String url, HttpMethod method,
            HandlerMethod handlerMethod) {
        if (menu != null) {
            StringBuffer caption = new StringBuffer();
            List<Binate<Integer, MenuItem>> indexes = menu.indexesOfItems(url, method);
            for (Binate<Integer, MenuItem> binate : indexes) {
                MenuItem item = binate.getRight();
                if (item instanceof ActableMenuItem) {
                    caption.append(" / ").append(((ActableMenuItem) item).getCaption());
                }
            }
            if (StringUtils.isNotBlank(caption)) {
                return caption.toString().trim();
            }
            // 如果无法从菜单中获得显示名称，则尝试从@Caption注解中获取，这意味着使用@Caption注解的方法将被记录操作日志
            Caption captionAnnotation = handlerMethod.getMethodAnnotation(Caption.class);
            if (captionAnnotation != null) {
                return captionAnnotation.value();
            }
        }
        return null;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
    }

    @Override
    public void beforeInvoke(String beanId, Method method, Object[] args) throws Exception {
    }

    @Override
    public void afterInvoke(String beanId, Method method, Object[] args, Object result) {
        K userId = getUserId();
        if (userId != null) { // 已登录的才需要记录日志
            String methodName = method.getName();
            int argCount = args.length;
            if (matches(beanId, methodName, argCount)) {
                LogExcluded logExcluded = method.getAnnotation(LogExcluded.class);
                if (logExcluded != null && logExcluded.value()) { // 整个方法都被排除
                    return;
                }
                // 在创建线程提交执行之前获取菜单，以免线程执行环境无法获取当前菜单
                Menu menu = getMenu();
                String caption = getRpcMethodCaption(menu, beanId, method, argCount);
                if (StringUtils.isNotBlank(caption)) { // 找得到对应显示名称的才记录日志
                    this.executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Action action = new Action(beanId, methodName, Arrays.asList(args));
                            action.setCaption(caption);
                            AbstractActionLogInterceptor.this.writer.add(userId, action);
                        }
                    });
                }
            }
        }
    }

    protected boolean matches(String beanId, String methodName, Integer argCount) {
        if (this.excludedRpcPatterns != null) {
            for (RpcPort pattern : this.excludedRpcPatterns) {
                if (pattern.matches(beanId, methodName, argCount)) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getRpcMethodCaption(Menu menu, String beanId, Method method, int argCount) {
        if (menu != null) {
            StringBuffer caption = new StringBuffer();
            List<Binate<Integer, MenuItem>> indexes = menu.indexesOfItems(beanId, method.getName(),
                    argCount);
            for (Binate<Integer, MenuItem> binate : indexes) {
                MenuItem item = binate.getRight();
                if (item instanceof ActableMenuItem) {
                    caption.append(" / ").append(((ActableMenuItem) item).getCaption());
                }
            }
            if (StringUtils.isNotBlank(caption)) {
                return caption.toString().trim();
            }
            // 如果无法从菜单中获得显示名称，则尝试从@Caption注解中获取，这意味着使用@Caption注解的方法将被记录操作日志
            Caption captionAnnotation = method.getAnnotation(Caption.class);
            if (captionAnnotation != null) {
                return captionAnnotation.value();
            }
        }
        return null;
    }

    protected abstract K getUserId();

    protected abstract Menu getMenu();
}
