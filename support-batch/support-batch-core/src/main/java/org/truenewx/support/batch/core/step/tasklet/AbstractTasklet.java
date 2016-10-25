package org.truenewx.support.batch.core.step.tasklet;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.StoppableTasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.truenewx.service.ServiceSupport;

/**
 * 抽象任务，提供终止支持
 *
 * @author jianglei
 * @since JDK 1.7
 */
public abstract class AbstractTasklet extends ServiceSupport implements StoppableTasklet {

    private boolean stopped;

    @Override
    public void stop() {
        this.stopped = true;
    }

    /**
     * 判断当前步骤是否应该终止
     *
     * @return 当前步骤是否应该终止
     */
    protected boolean shouldStop() {
        return this.stopped;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution,
            final ChunkContext chunkContext) throws Exception {
        final StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        this.stopped = false;
        final boolean completed = execute(stepExecution);
        this.stopped = true;
        if (!completed) {
            stepExecution.setStatus(BatchStatus.STOPPED); // 未完成相当于终止
        }
        return RepeatStatus.FINISHED;
    }

    /**
     * 执行步骤任务
     *
     * @param stepExecution
     *            当前步骤执行对象
     * @return 任务是否全部完成。如果中途终止则返回false，表示已完成的部分生效但未完成部分未执行
     * @throws Exception
     *             如果执行过程出现异常，意味着所有已完成的部分均被撤销，相当于未执行当前任务
     */
    protected abstract boolean execute(StepExecution stepExecution) throws Exception;

}
