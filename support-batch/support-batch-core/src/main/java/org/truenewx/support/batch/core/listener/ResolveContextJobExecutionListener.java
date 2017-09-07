package org.truenewx.support.batch.core.listener;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.truenewx.support.batch.core.job.context.util.JobContextHelper;
import org.truenewx.support.batch.core.step.tasklet.ProgressTasklet;
import org.truenewx.support.batch.step.progress.StepProgress;

/**
 * 解决上下文数据的作业执行侦听器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ResolveContextJobExecutionListener implements JobExecutionListener, InitializingBean {
    private ListableJobLocator jobRegistry;
    private JobContextHelper jobContextHelper;

    public void setJobRegistry(final ListableJobLocator jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    public void setJobContextHelper(final JobContextHelper jobContextHelper) {
        this.jobContextHelper = jobContextHelper;
    }

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        // 在作业开始之前，启动所有步骤的总数计算任务，以便于获取作业总进度
        this.jobContextHelper.setTotalling(jobExecution); // 标记作业正在计算总数
        final String jobName = jobExecution.getJobInstance().getJobName();
        try {
            final Job job = this.jobRegistry.getJob(jobName);
            // 因此时作业还未开始，步骤执行未建立，无法从作业执行中取得步骤执行
            if (job instanceof StepLocator) {
                final StepLocator stepLocator = (StepLocator) job;
                for (final String stepName : stepLocator.getStepNames()) {
                    final Step step = stepLocator.getStep(stepName);
                    if (step instanceof TaskletStep) {
                        final Tasklet tasklet = ((TaskletStep) step).getTasklet();
                        if (tasklet instanceof ProgressTasklet) {
                            // 获取步骤进度放入作业执行上下文中（无法放入步骤执行上下文）
                            final JobParameters parameters = jobExecution.getJobParameters();
                            try {
                                final StepProgress progress = ((ProgressTasklet<?>) tasklet)
                                        .createProgress(parameters);
                                this.jobContextHelper.setStepProgress(jobExecution, stepName,
                                        progress);
                            } catch (final Exception e) {
                                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        } catch (final JobExecutionException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        this.jobContextHelper.setTotalled(jobExecution); // 标记作业的总数计算已完成
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        this.jobContextHelper.releaseContext(jobExecution);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.jobRegistry, "jobRegistry must not be null");
        Assert.notNull(this.jobContextHelper, "jobContextHelper must not be null");
    }
}
