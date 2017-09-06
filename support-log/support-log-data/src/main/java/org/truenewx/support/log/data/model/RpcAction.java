package org.truenewx.support.log.data.model;

import java.util.List;

import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * RPC访问操作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcAction extends Action {

    private String beanId;
    private String methodName;
    private List<Object> args;

    public String getBeanId() {
        return this.beanId;
    }

    public void setBeanId(final String beanId) {
        this.beanId = beanId;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public List<Object> getArgs() {
        return this.args;
    }

    public void setArgs(final List<Object> args) {
        this.args = args;
    }

    @Override
    public String getType() {
        return "RPC";
    }

    @Override
    public int hashCode() {
        final Object[] array = { this.beanId, this.methodName, this.args };
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RpcAction other = (RpcAction) obj;
        return PredEqual.INSTANCE.apply(this.beanId, other.beanId)
                && PredEqual.INSTANCE.apply(this.methodName, other.methodName)
                && PredEqual.INSTANCE.apply(this.args, other.args);
    }
}
