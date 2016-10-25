package org.truenewx.support.batch.core.job.context.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.core.enums.Deviation;
import org.truenewx.core.tuple.DeviatedNumber;
import org.truenewx.support.batch.core.job.context.JobAttributeContext;
import org.truenewx.support.batch.step.progress.StepProgress;

/**
 * 作业上下文协助者
 *
 * @author jianglei
 * @since JDK 1.7
 */
@Component
public class JobContextHelper {

    private static final String STARTER = "starter";
    private static final String STEP_PROGRESS = "step.progress";
    private static final String TOTALLING = "totalling";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private JobRepository jobRepository;

    /**
     * 作业属性上下文映射集
     */
    // 作业执行的上下文在数据库中存取，导致线程直接修改内存中数据时，与数据库无法实时同步，故另建内存中存放的上下文
    private Map<Long, JobAttributeContext> jobContextMap = new Hashtable<>();

    private JobAttributeContext getJobContext(final JobExecution jobExecution) {
        final Long jobExecutionId = jobExecution.getId();
        JobAttributeContext jobContext;
        synchronized (this.jobContextMap) {
            jobContext = this.jobContextMap.get(jobExecutionId);
            if (jobContext == null) {
                jobContext = new JobAttributeContext();
                this.jobContextMap.put(jobExecutionId, jobContext);
                // 导入数据库数据
                for (final Entry<String, Object> entry : jobExecution.getExecutionContext()
                        .entrySet()) {
                    jobContext.setAttribute(entry.getKey(), entry.getValue());
                }
                for (final StepExecution stepExecution : jobExecution.getStepExecutions()) {
                    final String stepName = stepExecution.getStepName();
                    for (final Entry<String, Object> entry : stepExecution.getExecutionContext()
                            .entrySet()) {
                        jobContext.setStepAttribute(stepName, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return jobContext;
    }

    private void setJobAttribute(final JobExecution jobExecution, final String key,
            final Object value) {
        if (value != null) {
            getJobContext(jobExecution).setAttribute(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getJobAttribute(final JobExecution jobExecution, final String key) {
        return (T) getJobContext(jobExecution).getAttribute(key);
    }

    private void setStepAttribute(final JobExecution jobExecution, final String stepName,
            final String key, final Object value) {
        if (value != null) {
            getJobContext(jobExecution).setStepAttribute(stepName, key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getStepAttribute(final JobExecution jobExecution, final String stepName,
            final String key) {
        return (T) getJobContext(jobExecution).getStepAttribute(stepName, key);
    }

    private void setStepAttribute(final StepExecution stepExecution, final String key,
            final Object value) {
        setStepAttribute(stepExecution.getJobExecution(), stepExecution.getStepName(), key, value);
    }

    private <T> T getStepAttribute(final StepExecution stepExecution, final String key) {
        return getStepAttribute(stepExecution.getJobExecution(), stepExecution.getStepName(), key);
    }

    /**
     * 保存指定作业步骤进度到指定作业步骤上下文中
     *
     * @param progress
     *            作业步骤进度
     * @param context
     *            作业步骤上下文中
     */
    public void setStepProgress(final StepExecution stepExecution, final StepProgress progress) {
        setStepAttribute(stepExecution, STEP_PROGRESS, progress);
    }

    public void setStepProgress(final JobExecution jobExecution, final String stepName,
            final StepProgress progress) {
        setStepAttribute(jobExecution, stepName, STEP_PROGRESS, progress);
    }

    /**
     * 获取存放在指定作业执行的上下文中的步骤进度映射集，会先后从内存和数据库中获取数据
     *
     * @param jobExecution
     *            作业执行
     * @return 步骤进度映射集
     */
    public Map<String, StepProgress> getStepProgressMap(final JobExecution jobExecution) {
        final Map<String, StepProgress> map = new HashMap<>();
        // 先从内存上下文中获取
        final JobAttributeContext jobContext = getJobContext(jobExecution);
        for (final String stepName : jobContext.getStepNames()) {
            final StepProgress progress = jobContext.getStepAttribute(stepName, STEP_PROGRESS);
            if (progress != null) {
                map.put(stepName, progress);
            }
        }
        // 再从数据库中获取，如果在执行过程中有内存中的步骤进度进入数据库，会出现数据库数据覆盖内存数据的情况，
        // 这种情况不会有问题，但如果先从数据库中获取，则可能出现遗漏步骤执行的情况
        for (final StepExecution stepExecution : jobExecution.getStepExecutions()) {
            final StepProgress progress = (StepProgress) stepExecution.getExecutionContext()
                    .get(STEP_PROGRESS);
            if (progress != null) {
                map.put(stepExecution.getStepName(), progress);
            }
        }
        return map;
    }

    /**
     * 从指定作业步骤上下文中获取作业步骤进度
     *
     * @param stepExecution
     *            作业步骤执行对象
     * @return 作业步骤进度
     */
    public <T extends StepProgress> T getStepProgress(final StepExecution stepExecution) {
        return getStepAttribute(stepExecution, STEP_PROGRESS);
    }

    /**
     * 将指定启动者保存到指定作业执行对象中
     *
     * @param jobExecution
     *            作业执行对象
     * @param starter
     *            启动者
     */
    public void setStarter(final JobExecution jobExecution, final Serializable starter) {
        setJobAttribute(jobExecution, STARTER, starter);
    }

    /**
     * 从指定作业执行对象中获取启动者
     *
     * @param jobExecution
     *            作业执行对象
     * @return 启动者
     */
    public Serializable getStarter(final JobExecution jobExecution) {
        return getJobAttribute(jobExecution, STARTER);
    }

    /**
     * 从指定作业执行对象中获取作业总数
     *
     * @param jobExecution
     *            作业执行对象
     */
    public DeviatedNumber<Integer> getJobTotal(final JobExecution jobExecution) {
        Integer jobTotal = null;
        Deviation deviation = Deviation.NONE; // 默认无偏差
        final Map<String, StepProgress> progressMap = getStepProgressMap(jobExecution);
        if (progressMap.isEmpty()) {
            LoggerFactory.getLogger(getClass()).warn(
                    "Empty progress map when getJobTotal for job with name={} and executionId={}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId());
        }
        for (final StepProgress progress : progressMap.values()) {
            final int stepTotal = progress.getTotal();
            if (stepTotal >= 0) {
                if (jobTotal == null) {
                    jobTotal = stepTotal;
                } else {
                    jobTotal += stepTotal;
                }
            } else { // 只要存在有一个总数无法确定，则偏差就为偏大于
                deviation = Deviation.GREATER;
            }
        }
        return new DeviatedNumber<>(jobTotal, deviation);
    }

    /**
     * 设置正在计算总数
     *
     * @param jobExecution
     *            作业执行对象
     */
    public void setTotalling(final JobExecution jobExecution) {
        setJobAttribute(jobExecution, TOTALLING, Boolean.TRUE);
        this.logger.info("job is totalling whose name={} and executionId={}",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
    }

    /**
     * 判断是否正在计算总数
     *
     * @param jobExecution
     *            作业执行对象
     * @return 是否正在计算总数
     */
    public boolean isTotalling(final JobExecution jobExecution) {
        final Object obj = getJobAttribute(jobExecution, TOTALLING);
        return Boolean.TRUE.equals(obj);
    }

    /**
     * 设置已经完成总数计算
     *
     * @param jobExecution
     *            作业执行对象
     */
    public void setTotalled(final JobExecution jobExecution) {
        setJobAttribute(jobExecution, TOTALLING, Boolean.FALSE);
        this.logger.info("job has totalled whose name={} and executionId={}",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
    }

    /**
     * 判断是否已经完成总数计算
     *
     * @param jobExecution
     *            作业执行对象
     * @return 是否已经完成总数计算
     */
    public boolean isTotalled(final JobExecution jobExecution) {
        final Object obj = getJobAttribute(jobExecution, TOTALLING);
        return Boolean.FALSE.equals(obj);
    }

    /**
     * 判断是否还未开始计算总数
     *
     * @param jobExecution
     *            作业执行对象
     * @return 是否还未开始计算总数
     */
    public boolean isUntotal(final JobExecution jobExecution) {
        return getJobAttribute(jobExecution, TOTALLING) == null;
    }

    /**
     * 释放内存中指定作业执行对应的上下文
     *
     * @param jobExecution
     *            作业执行
     */
    public void releaseContext(final JobExecution jobExecution) {
        final JobAttributeContext jobContext = this.jobContextMap.get(jobExecution.getId());
        // 内存上下文导出到数据库
        for (final String key : jobContext.attributeNames()) {
            jobExecution.getExecutionContext().put(key, jobContext.getAttribute(key));
        }
        this.jobRepository.updateExecutionContext(jobExecution);
        for (final StepExecution stepExecution : jobExecution.getStepExecutions()) {
            final String stepName = stepExecution.getStepName();
            for (final String key : jobContext.getStepAttributeNames(stepName)) {
                stepExecution.getExecutionContext().put(key,
                        jobContext.getStepAttribute(stepName, key));
            }
            this.jobRepository.updateExecutionContext(stepExecution);
        }
        // 释放内存中的作业上下文
        this.jobContextMap.remove(jobExecution.getId());
        // 先保存数据库再释放内存，以避免内存已释放但尚未保存到数据库时，读取数据的遗漏
        this.logger.info("job has released context whose name={} and executionId={}",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
    }

}
