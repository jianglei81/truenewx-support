package org.truenewx.support.log.data.model;

import java.util.List;
import java.util.Map;

import org.truenewx.data.model.ValueModel;

/**
 * 操作
 *
 * @author jianglei
 * @since JDK 1.8
 */
// 不以子类继承体系的方式构建，是因为值模型在Hibernate中不能以继承方式配置，而不以值模型的方式则需要另外创建动作表
public class Action implements ValueModel {

    private String caption;
    private String beanId;
    private String url;
    private String method;
    private Map<String, Object> params;
    private List<Object> args;

    protected Action() {
    }

    public Action(String beanId, String url, String method, Map<String, Object> params) {
        this.beanId = beanId;
        this.url = url;
        this.method = method;
        this.params = params;
    }

    public Action(String beanId, String method, List<Object> args) {
        this.beanId = beanId;
        this.method = method;
        this.args = args;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getBeanId() {
        return this.beanId;
    }

    protected void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getUrl() {
        return this.url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return this.method;
    }

    protected void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    protected void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<Object> getArgs() {
        return this.args;
    }

    protected void setArgs(List<Object> args) {
        this.args = args;
    }

    public String getType() {
        return this.url != null ? "URL" : "RPC";
    }

}
