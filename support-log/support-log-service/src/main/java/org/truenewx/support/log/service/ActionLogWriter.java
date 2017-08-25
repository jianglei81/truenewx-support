package org.truenewx.support.log.service;

import java.io.Serializable;

import org.truenewx.support.log.data.model.Action;

/**
 * 操作日志记录器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface ActionLogWriter<K extends Serializable> {

    void add(K userId, Action action);

}
