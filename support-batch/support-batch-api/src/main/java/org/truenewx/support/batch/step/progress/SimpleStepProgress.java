package org.truenewx.support.batch.step.progress;

import java.io.Serializable;

/**
 * 简单的步骤进度，仅包含成功和失败数量
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SimpleStepProgress extends StepProgress {

    private static final long serialVersionUID = -9089547531587265405L;

    private int successCount;
    private int failureCount;

    /**
     *
     * @param total
     *            要处理的对象总数
     */
    public SimpleStepProgress(final int total) {
        super(total);
    }

    @Override
    public int getSuccessCount() {
        return this.successCount;
    }

    @Override
    public Iterable<Serializable> getSuccesses() {
        return null;
    }

    @Override
    public int getFailureCount() {
        return this.failureCount;
    }

    @Override
    public Iterable<Serializable> getFailures() {
        return null;
    }

    @Override
    public Exception getFailureException(final Serializable failure) {
        return null;
    }

    /**
     * 递增成功数
     *
     * @return 递增之后的成功数
     */
    public int increaseSuccess() {
        return ++this.successCount;
    }

    /**
     * 递增失败数
     *
     * @return 递增之后的失败数
     */
    public int increaseFailures() {
        return ++this.failureCount;
    }

}
