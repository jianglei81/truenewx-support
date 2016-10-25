package org.truenewx.support.batch.core.step.tasklet;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.support.batch.core.job.context.util.JobContextHelper;
import org.truenewx.support.batch.step.progress.StepProgress;

/**
 * 关心进度的任务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <P>
 *            步骤进度类型
 */
public abstract class ProgressTasklet<P extends StepProgress> extends AbstractTasklet {

    private JobContextHelper jobContextHelper;

    @Autowired(required = false)
    public void setJobContextHelper(final JobContextHelper jobContextHelper) {
        this.jobContextHelper = jobContextHelper;
    }

    public P createProgress(final JobParameters parameters) throws Exception {
        final int total = resolveTotal(parameters);
        return newProgress(total);
    }

    protected abstract int resolveTotal(JobParameters parameters);

    /**
     * 用指定步骤总数创建步骤进度对象
     *
     * @param total
     *            步骤总数
     *
     * @return 步骤进度
     */
    protected P newProgress(final int total) throws Exception {
        final Class<P> progressType = ClassUtil.getActualGenericType(getClass(), 0);
        return progressType.getConstructor(int.class).newInstance(total);
    }

    @Override
    protected boolean execute(final StepExecution stepExecution) throws Exception {
        final JobParameters parameters = stepExecution.getJobParameters();
        final P progress = this.jobContextHelper.getStepProgress(stepExecution);
        return execute(parameters, progress);
    }

    /**
     * 执行关注进度的步骤任务
     *
     * @param parameters
     *            作业参数集
     * @param progress
     *            步骤进度
     * @return 任务是否全部完成。如果中途终止则返回false，表示已完成的部分生效但未完成部分未执行
     * @throws Exception
     *             如果执行过程出现异常，意味着所有已完成的部分均被撤销，相当于未执行当前任务
     */
    protected abstract boolean execute(JobParameters parameters, P progress) throws Exception;

}
