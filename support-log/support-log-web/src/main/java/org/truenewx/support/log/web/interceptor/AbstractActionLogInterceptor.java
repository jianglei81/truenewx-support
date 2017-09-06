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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.tuple.Binate;
import org.truenewx.support.log.data.model.RpcAction;
import org.truenewx.support.log.data.model.UrlAction;
import org.truenewx.support.log.service.ActionLogWriter;
import org.truenewx.support.log.web.annotation.LogExcluded;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuAction;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.rpc.server.RpcInvokeInterceptor;
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

    public void setExcludedUrlPatterns(final String[] excludedUrlPatterns) {
        if (excludedUrlPatterns != null) {
            this.excludedUrlPatterns = new ArrayList<>();
            for (final String pattern : excludedUrlPatterns) {
                final String[] pair = pattern.split(Strings.SEMICOLON);
                final String url = pair[0];
                HttpMethod method = null;
                if (pair.length > 1) {
                    method = EnumUtils.getEnum(HttpMethod.class, pair[1]);
                }
                this.excludedUrlPatterns.add(new HttpLink(url, method));
            }
        }
    }

    public void setExcludedRpcPatterns(final String[] excludedRpcPatterns) {
        if (excludedRpcPatterns != null) {
            this.excludedRpcPatterns = new ArrayList<>();
            for (final String pattern : excludedRpcPatterns) {
                final String[] pair = pattern.split("\\.");
                final String beanId = pair[0];
                String methodName = Strings.ASTERISK;
                Integer argCount = null;
                if (pair.length > 1) {
                    final int index = pair[1].indexOf(Strings.LEFT_BRACKET);
                    if (index < 0) {
                        methodName = pair[1];
                    } else {
                        methodName = pair[1].substring(0, index);
                        final String argString = pair[1].substring(index + 1,
                                pair[1].indexOf(Strings.RIGHT_BRACKET));
                        argCount = argString.split(Strings.COMMA).length;
                    }
                }
                final RpcPort rpcPort = argCount == null ? new RpcPort(beanId, methodName)
                        : new RpcPort(beanId, methodName, argCount);
                this.excludedRpcPatterns.add(rpcPort);
            }
        }
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        return true;
    }

    protected boolean matches(final String url, final HttpMethod method) {
        if (url.startsWith("/rpc/")) { // 忽略所有RPC请求
            return false;
        }
        if (this.excludedUrlPatterns != null) {
            for (final HttpLink pattern : this.excludedUrlPatterns) {
                if (pattern.isMatched(url, method)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final ModelAndView modelAndView) throws Exception {
        // 请求失败或为include请求则忽略当前拦截器
        if (response.getStatus() != HttpServletResponse.SC_OK
                || WebUtils.isIncludeRequest(request)) {
            return;
        }
        final K userId = getUserId();
        if (userId != null) { // 已登录的才需要记录日志
            final String url = WebUtil.getRelativeRequestUrl(request);
            final HttpMethod method = EnumUtils.getEnum(HttpMethod.class, request.getMethod());
            if (matches(url, method)) { // URL匹配才进行校验
                final String[] excludedParameterNames;
                if (handler instanceof HandlerMethod) {
                    final HandlerMethod hm = (HandlerMethod) handler;
                    final LogExcluded logExcluded = hm.getMethodAnnotation(LogExcluded.class);
                    if (logExcluded != null) {
                        excludedParameterNames = logExcluded.parameters();
                        if (excludedParameterNames.length == 0) { // 存在LogExcluded注解但未指定排除参数集，则整个方法被排除
                            return;
                        }
                    } else {
                        excludedParameterNames = new String[0];
                    }
                } else {
                    excludedParameterNames = new String[0];
                }
                final Menu menu = getMenu(); // 在创建线程提交执行之前获取菜单，以免线程执行环境无法获取当前菜单
                this.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final UrlAction action = new UrlAction();
                        action.setUrl(url);
                        action.setMethod(method.name());
                        action.setCaption(getUrlActionCaption(menu, url, method));
                        final Map<String, Object> params = WebUtil.getRequestParameterMap(request,
                                excludedParameterNames);
                        if (!params.isEmpty()) {
                            action.setParams(params);
                        }
                        AbstractActionLogInterceptor.this.writer.add(userId, action);
                    }
                });
            }
        }
    }

    private String getUrlActionCaption(final Menu menu, final String url, final HttpMethod method) {
        if (menu != null) {
            final StringBuffer caption = new StringBuffer();
            final List<Binate<Integer, MenuAction>> indexes = menu.indexesOf(url, method);
            for (final Binate<Integer, MenuAction> binate : indexes) {
                caption.append(" / ").append(binate.getRight().getCaption());
            }
            return caption.length() == 0 ? null : caption.toString().trim();
        }
        return null;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception ex)
            throws Exception {
    }

    @Override
    public void beforeInvoke(final String beanId, final Method method, final Object[] args)
            throws Exception {
    }

    @Override
    public void afterInvoke(final String beanId, final Method method, final Object[] args,
            final Object result) {
        final K userId = getUserId();
        if (userId != null) { // 已登录的才需要记录日志
            final String methodName = method.getName();
            final int argCount = args.length;
            if (matches(beanId, methodName, argCount)) {
                if (method.getAnnotation(LogExcluded.class) != null) {
                    // RPC方法只要存在LogExcluded注解，则不论有没有指定忽略参数集，均忽略该方法
                    return;
                }
                final Menu menu = getMenu(); // 在创建线程提交执行之前获取菜单，以免线程执行环境无法获取当前菜单
                this.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final RpcAction action = new RpcAction();
                        action.setBeanId(beanId);
                        action.setMethodName(methodName);
                        final List<Object> argList = Arrays.asList(args);
                        if (!argList.isEmpty()) {
                            action.setArgs(argList);
                        }
                        action.setCaption(getRpcActionCaption(menu, beanId, methodName, argCount));
                        AbstractActionLogInterceptor.this.writer.add(userId, action);
                    }
                });
            }
        }
    }

    protected boolean matches(final String beanId, final String methodName,
            final Integer argCount) {
        if (this.excludedRpcPatterns != null) {
            for (final RpcPort pattern : this.excludedRpcPatterns) {
                if (pattern.isMatched(beanId, methodName, argCount)) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getRpcActionCaption(final Menu menu, final String beanId,
            final String methodName, final int argCount) {
        if (menu != null) {
            final StringBuffer caption = new StringBuffer();
            final List<Binate<Integer, MenuAction>> indexes = menu.indexesOf(beanId, methodName,
                    argCount);
            for (final Binate<Integer, MenuAction> binate : indexes) {
                caption.append(" / ").append(binate.getRight().getCaption());
            }
            return caption.length() == 0 ? null : caption.toString().trim();
        }
        return null;
    }

    protected abstract K getUserId();

    protected abstract Menu getMenu();
}
