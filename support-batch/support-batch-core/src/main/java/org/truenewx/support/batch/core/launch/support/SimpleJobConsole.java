package org.truenewx.support.batch.core.launch.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.truenewx.core.tuple.DeviatedNumber;
import org.truenewx.support.batch.core.JobSummary;
import org.truenewx.support.batch.core.converter.ClassBasedJobParametersConverter;
import org.truenewx.support.batch.core.job.context.util.JobContextHelper;
import org.truenewx.support.batch.core.launch.JobConsole;

/**
 * 简单的作业控制台
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SimpleJobConsole implements JobConsole, InitializingBean {

    private ListableJobLocator jobRegistry;
    private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;
    private JobRepository jobRepository;
    private JobParametersConverter jobParametersConverter = new ClassBasedJobParametersConverter();
    private JobOperator jobOperator;
    private JobContextHelper jobContextHelper;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setJobRegistry(final ListableJobLocator jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    public void setJobExplorer(final JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public void setJobLauncher(final JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobRepository(final JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void setJobParametersConverter(final JobParametersConverter jobParametersConverter) {
        this.jobParametersConverter = jobParametersConverter;
    }

    public void setJobOperator(final JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    public void setJobContextHelper(final JobContextHelper jobContextHelper) {
        this.jobContextHelper = jobContextHelper;
    }

    @Override
    public Collection<String> getJobNames() {
        return this.jobRegistry.getJobNames();
    }

    @Override
    public Long start(final String jobName, final Properties params, final Serializable starter) {
        this.logger.info("Checking status of job with name={}", jobName);
        try {
            final JobParameters jobParameters = this.jobParametersConverter
                    .getJobParameters(params);

            if (this.jobRepository.isJobInstanceExists(jobName, jobParameters)) { // 已经启动则忽略直接返回作业执行id
                final JobExecution jobExecution = this.jobRepository.getLastJobExecution(jobName,
                        jobParameters);
                return jobExecution == null ? null : jobExecution.getId();
            }

            final Job job = this.jobRegistry.getJob(jobName);

            this.logger.info("Attempting to launch job with name={} and parameters={}", jobName,
                    params);
            final JobExecution jobExecution = this.jobLauncher.run(job, jobParameters);
            this.jobContextHelper.setStarter(jobExecution, starter); // 保存启动者
            final Long executionId = jobExecution.getId();
            this.logger.info("Launched job with name={} and parameters={} and executionId={}",
                    jobName, params, executionId);
            return executionId;
        } catch (final Exception e) {
            handleErrorMessage(jobName, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean stop(final long id) {
        try {
            return this.jobOperator.stop(id);
        } catch (final Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void abandon(final long id) {
        try {
            this.jobOperator.abandon(id);
        } catch (final Exception e) {
            this.logger.error(e.getMessage(), e);
        }
    }

    @Override
    public Long restart(final long id) {
        try {
            return this.jobOperator.restart(id);
        } catch (final Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public DeviatedNumber<Integer> getTotal(final long id, final long timeout) {
        final JobExecution jobExecution = findJobExecutionById(id);
        if (jobExecution != null) {
            final long time = System.currentTimeMillis();
            if (timeout > 0) { // 超时时间大于0才需要等待
                final long interval = timeout > 100 ? 100 : timeout; // 等待间隔时间最多100毫秒
                // 总数计算未完成则等待
                while (!this.jobContextHelper.isTotalled(jobExecution)) {
                    try {
                        Thread.sleep(interval);
                    } catch (final InterruptedException e) {
                        this.logger.error(e.getMessage(), e);
                    }
                    // 等待总时间超过指定超时时间，则不再等待
                    if (System.currentTimeMillis() - time > timeout) {
                        break;
                    }
                }
            }
            // 总数可能未计算或无法完成，此时返回null
            return this.jobContextHelper.getJobTotal(jobExecution);
        }
        return null;
    }

    @Override
    public JobSummary getSummary(final long id) {
        final JobExecution jobExecution = findJobExecutionById(id);
        if (jobExecution != null) {
            return new JobSummary(this.jobContextHelper, jobExecution);
        }
        return null;
    }

    @Override
    public int countRunning(final Serializable starter) {
        int count = 0;
        for (final String jobName : getJobNames()) {
            for (final JobExecution jobExecution : this.jobExplorer
                    .findRunningJobExecutions(jobName)) {
                if (starter.equals(this.jobContextHelper.getStarter(jobExecution))) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public List<JobSummary> findRunning(final Serializable starter) {
        // 取得所有满足条件的作业执行对象
        final List<JobSummary> list = new ArrayList<>();
        for (final String jobName : getJobNames()) {
            for (final JobExecution jobExecution : this.jobExplorer
                    .findRunningJobExecutions(jobName)) {
                if (starter.equals(this.jobContextHelper.getStarter(jobExecution))) {
                    list.add(new JobSummary(this.jobContextHelper, jobExecution));
                }
            }
        }
        return list;
    }

    private void handleErrorMessage(final String jobName, final String message) {
        this.logger.error("jobName={},message={}", jobName, message);
    }

    private JobExecution findJobExecutionById(final long executionId) {
        final JobExecution jobExecution = this.jobExplorer.getJobExecution(executionId);
        if (jobExecution == null) {
            this.logger.error("No JobExecution found for id: [{}]", executionId);
        }
        return jobExecution;
    }

    @Override
    public Boolean isEnd(final long id) {
        final JobExecution jobExecution = findJobExecutionById(id);
        if (jobExecution != null) {
            return jobExecution.getEndTime() != null;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.jobLauncher, "JobLauncher must be provided");
        Assert.notNull(this.jobRegistry, "JobLocator must be provided");
        Assert.notNull(this.jobExplorer, "JobExplorer must be provided");
        Assert.notNull(this.jobRepository, "JobRepository must be provided");
        Assert.notNull(this.jobContextHelper, "ExecutionContextHelper must be provided");
    }
}
