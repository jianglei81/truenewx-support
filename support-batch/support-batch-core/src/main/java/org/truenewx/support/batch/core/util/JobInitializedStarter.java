package org.truenewx.support.batch.core.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.support.batch.core.launch.JobConsole;

/**
 * 作业初始化启动器<br/>
 * 用于在系统启动初始启动作业
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class JobInitializedStarter implements ContextInitializedBean {

    private JobConsole jobConsole;
    private Map<String, Map<String, Object>> jobParameters;

    @Autowired
    public void setJobConsole(final JobConsole jobConsole) {
        this.jobConsole = jobConsole;
    }

    public void setJobParameters(final Map<String, Map<String, Object>> jobParameters) {
        this.jobParameters = jobParameters;
    }

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        if (this.jobParameters != null) {
            for (final Entry<String, Map<String, Object>> entry : this.jobParameters.entrySet()) {
                final String jobName = entry.getKey();
                final Properties params = BatchUtil.newUniqueParameters();
                params.putAll(entry.getValue());
                this.jobConsole.start(jobName, params, null);
            }
        }
    }

}
