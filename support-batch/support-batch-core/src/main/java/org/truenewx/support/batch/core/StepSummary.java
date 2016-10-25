package org.truenewx.support.batch.core;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.StepExecution;
import org.truenewx.support.batch.step.progress.StepProgress;

/**
 * 作业步骤汇总摘要
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class StepSummary extends BatchSummary {

    private static final long serialVersionUID = -1672949164250972676L;

    private String stepName;
    private StepProgress progress;

    public StepSummary(final StepExecution stepExecution, final StepProgress progress) {
        this.stepName = stepExecution.getStepName();
        this.startTime = stepExecution.getStartTime();
        this.endTime = stepExecution.getEndTime();
        this.status = stepExecution.getStatus();
        this.progress = progress;
    }

    public StepSummary(final String stepName, final StepProgress progress) {
        this.stepName = stepName;
        this.status = BatchStatus.UNKNOWN;
        this.progress = progress;
    }

    public String getStepName() {
        return this.stepName;
    }

    public StepProgress getProgress() {
        return this.progress;
    }
}
