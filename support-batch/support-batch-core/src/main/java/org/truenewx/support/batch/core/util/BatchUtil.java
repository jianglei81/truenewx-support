package org.truenewx.support.batch.core.util;

import java.util.Properties;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.truenewx.core.util.StringUtil;
import org.truenewx.support.batch.core.UniqueJobParametersBuilder;

/**
 * 批量任务工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class BatchUtil {

    private static long JOB_EXECUTION_ID = 0;

    private BatchUtil() {
    }

    /**
     * 创建唯一的参数集。批量处理框架对同样的参数集只执行一次，本方法提供每次均可被执行的参数集
     *
     * @return 唯一的参数集
     */
    public static Properties newUniqueParameters() {
        final Properties properties = new Properties();
        properties.setProperty(UniqueJobParametersBuilder.UUID, StringUtil.uuid32());
        return properties;
    }

    /**
     * 创建一个模拟的步骤执行对象
     *
     * @param builder
     *            作业参数集构建器
     * @return 步骤执行对象
     */
    public static StepExecution mockStepExecution(final JobParametersBuilder builder) {
        final JobExecution jobExecution = new JobExecution(JOB_EXECUTION_ID++,
                builder.toJobParameters());
        return new StepExecution(StringUtil.uuid32(), jobExecution);
    }

}
