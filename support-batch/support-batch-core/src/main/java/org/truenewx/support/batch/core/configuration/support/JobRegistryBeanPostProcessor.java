package org.truenewx.support.batch.core.configuration.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.xml.StepParserStepFactoryBean;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.BeansException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.truenewx.support.batch.core.job.annotation.AutoJob;
import org.truenewx.support.batch.item.Itemlet;

/**
 * 作业注册提交处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JobRegistryBeanPostProcessor
        extends org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor {
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;
    private JobExecutionListener jobExecutionListener;

    public void setTransactionManager(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setJobRepository(final JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void setJobExecutionListener(final JobExecutionListener jobExecutionListener) {
        this.jobExecutionListener = jobExecutionListener;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
            throws BeansException {
        final Object result = super.postProcessAfterInitialization(bean, beanName);
        if (bean instanceof Tasklet) {
            final Job job = buildAutoJob(beanName, (Tasklet) bean);
            if (job != null) {
                super.postProcessAfterInitialization(job, job.getName());
            }
            return bean;
        } else if (bean instanceof Itemlet) {
            final Itemlet<?, ?> batchlet = (Itemlet<?, ?>) bean;
            final Job job = buildAutoJob(beanName, batchlet);
            if (job != null) {
                super.postProcessAfterInitialization(job, job.getName());
            }
            return bean;
        }
        return result;
    }

    private String getPrefix(final String s, final String suffix) {
        if (s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        } else {
            return s;
        }
    }

    private Job buildAutoJob(final String beanName, final Tasklet tasklet) {
        final AutoJob autoJob = tasklet.getClass().getAnnotation(AutoJob.class);
        if (autoJob != null) {
            final String namePrefix = getPrefix(beanName, Tasklet.class.getSimpleName());
            // 构建步骤
            final TaskletStep step = new TaskletStep(namePrefix + Step.class.getSimpleName());
            step.setTasklet(tasklet);
            step.setTransactionManager(this.transactionManager);
            step.setJobRepository(this.jobRepository);
            // 构建作业
            String jobName = autoJob.jobName();
            if (StringUtils.isBlank(jobName)) {
                jobName = namePrefix + Job.class.getSimpleName();
            }
            final SimpleJob job = new SimpleJob(jobName);
            job.addStep(step);
            job.setRestartable(true);
            job.setJobRepository(this.jobRepository);
            if (this.jobExecutionListener != null) {
                job.registerJobExecutionListener(this.jobExecutionListener);
            }
            return job;
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Job buildAutoJob(final String beanName, final Itemlet<?, ?> itemlet) {
        final AutoJob autoJob = itemlet.getClass().getAnnotation(AutoJob.class);
        if (autoJob != null) {
            final String namePrefix = getPrefix(beanName, Itemlet.class.getSimpleName());
            // 构建步骤
            final StepParserStepFactoryBean factoryBean = new StepParserStepFactoryBean();
            factoryBean.setTransactionManager(this.transactionManager);
            factoryBean.setJobRepository(this.jobRepository);
            factoryBean.setHasChunkElement(true);
            factoryBean.setName(namePrefix + Step.class.getSimpleName()); // 步骤名称
            factoryBean.setItemReader(itemlet);
            factoryBean.setItemProcessor(itemlet);
            factoryBean.setItemWriter(itemlet);
            final int commitInterval = itemlet.getCommitInterval();
            if (commitInterval > 0) {
                factoryBean.setCommitInterval(commitInterval);
            }
            try {
                final Step step = factoryBean.getObject();
                // 构建作业
                String jobName = autoJob.jobName();
                if (StringUtils.isBlank(jobName)) {
                    jobName = namePrefix + Job.class.getSimpleName();
                }
                final SimpleJob job = new SimpleJob(jobName);
                job.addStep(step);
                job.setRestartable(true);
                job.setJobRepository(this.jobRepository);
                return job;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.transactionManager, "TransactionManager must not be null");
        Assert.notNull(this.jobRepository, "JobRepository must not be null");
    }

}
