package org.truenewx.support.batch.core.job.context;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.repeat.context.SynchronizedAttributeAccessor;
import org.springframework.core.AttributeAccessor;

/**
 * 作业属性上下文，用于在内存中存放作业上下文，不保存至数据库
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class JobAttributeContext extends SynchronizedAttributeAccessor {

    private Map<String, AttributeAccessor> stepContextMap = new Hashtable<>(); // 确保同步安全

    private AttributeAccessor getStepContext(final String stepName) {
        AttributeAccessor stepContext;
        synchronized (this.stepContextMap) {
            stepContext = this.stepContextMap.get(stepName);
            if (stepContext == null) {
                stepContext = new SynchronizedAttributeAccessor();
                this.stepContextMap.put(stepName, stepContext);
            }
        }
        return stepContext;
    }

    public void setStepAttribute(final String stepName, final String name, final Object value) {
        getStepContext(stepName).setAttribute(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getStepAttribute(final String stepName, final String name) {
        return (T) getStepContext(stepName).getAttribute(name);
    }

    public Set<String> getStepNames() {
        return this.stepContextMap.keySet();
    }

    public String[] getStepAttributeNames(final String stepName) {
        return getStepContext(stepName).attributeNames();
    }

}
