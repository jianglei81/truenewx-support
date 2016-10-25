package org.truenewx.support.batch.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.core.JobExecution;
import org.truenewx.core.enums.Deviation;
import org.truenewx.core.tuple.DeviatedNumber;
import org.truenewx.support.batch.core.job.context.util.JobContextHelper;
import org.truenewx.support.batch.step.progress.StepProgress;

/**
 * 作业汇总摘要
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JobSummary extends BatchSummary {

    private static final long serialVersionUID = 4388502276350001066L;

    private Long id;
    private Serializable starter;
    private List<StepSummary> stepSummaries = new ArrayList<>();

    public JobSummary(final JobContextHelper jobContextHelper, final JobExecution jobExecution) {
        this.id = jobExecution.getId();
        this.starter = jobContextHelper.getStarter(jobExecution);
        this.startTime = jobExecution.getStartTime();
        this.endTime = jobExecution.getEndTime();
        this.status = jobExecution.getStatus();
        final Map<String, StepProgress> progressMap = jobContextHelper
                .getStepProgressMap(jobExecution);
        for (final Entry<String, StepProgress> entry : progressMap.entrySet()) {
            final String stepName = entry.getKey();
            final StepProgress progress = entry.getValue();
            this.stepSummaries.add(new StepSummary(stepName, progress));
        }
    }

    public Long getId() {
        return this.id;
    }

    public Serializable getStarter() {
        return this.starter;
    }

    public List<StepSummary> getStepSummaries() {
        return this.stepSummaries;
    }

    /**
     * 获取唯一的作业步骤进度
     *
     * @return 唯一的作业步骤进度，有且仅有一个作业步骤进度时返回该进度，否则返回null
     */
    public StepProgress getUniqueStepProgress() {
        if (this.stepSummaries.size() == 1) {
            return this.stepSummaries.get(0).getProgress();
        }
        return null;
    }

    /**
     * 获取作业总数，由各步骤总数相加所得，步骤总数为null的忽略
     *
     * @return 作业总数
     */
    public DeviatedNumber<Integer> getTotal() {
        Integer total = null;
        Deviation deviation = Deviation.NONE; // 默认无偏差
        for (final StepSummary stepSummary : this.stepSummaries) {
            final StepProgress progress = stepSummary.getProgress();
            if (progress != null) {
                final int stepTotal = progress.getTotal();
                if (stepTotal >= 0) {
                    if (total == null) {
                        total = stepTotal;
                    } else {
                        total += stepTotal;
                    }
                } else { // 只要存在有一个总数无法确定，则偏差就为偏大于
                    deviation = Deviation.GREATER;
                }
            }
        }
        return new DeviatedNumber<>(total, deviation);
    }

    /**
     * 获取作业成功总数，由各步骤总数相加所得
     *
     * @return 成功总数
     */
    public int getSuccessCount() {
        int count = 0;
        for (final StepSummary stepSummary : this.stepSummaries) {
            final StepProgress progress = stepSummary.getProgress();
            if (progress != null) {
                count += progress.getSuccessCount();
            }
        }
        return count;
    }

    /**
     * 获取作业失败总数，由各步骤总数相加所得
     *
     * @return 成功总数
     */
    public int getFailureCount() {
        int count = 0;
        for (final StepSummary stepSummary : this.stepSummaries) {
            final StepProgress progress = stepSummary.getProgress();
            if (progress != null) {
                count += progress.getFailureCount();
            }
        }
        return count;
    }
}
