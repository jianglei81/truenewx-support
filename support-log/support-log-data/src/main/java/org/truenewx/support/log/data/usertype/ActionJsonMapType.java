package org.truenewx.support.log.data.usertype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.hibernate.usertype.ObjectJsonMapType;
import org.truenewx.support.log.data.model.Action;
import org.truenewx.support.log.data.model.RpcAction;
import org.truenewx.support.log.data.model.UrlAction;

/**
 * 操作对象-JSON字符串的映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ActionJsonMapType extends ObjectJsonMapType {

    @Override
    public void setParameterValues(final Properties parameters) {
        // 无需设置任何参数
    }

    @Override
    public Class<?> returnedClass() {
        return Action.class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SessionImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            try {
                Class<?> type = null;
                if (value.contains("\"type\":\"URL\"")) {
                    type = UrlAction.class;
                } else if (value.contains("\"type\":\"RPC\"")) {
                    type = RpcAction.class;
                }
                if (type != null) {
                    return JsonUtil.json2Bean(value, type);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
