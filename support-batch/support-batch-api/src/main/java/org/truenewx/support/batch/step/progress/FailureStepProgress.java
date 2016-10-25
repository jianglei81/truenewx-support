package org.truenewx.support.batch.step.progress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 仅包含失败对象清单的步骤进度
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FailureStepProgress extends StepProgress {

    private static final long serialVersionUID = -1003064155780128443L;

    private Collection<Serializable> failures = new ArrayList<>();

    /**
     * @param total
     *            要处理的对象总数
     */
    public FailureStepProgress(final int total) {
        super(total);
    }

    @Override
    public int getSuccessCount() {
        return 0;
    }

    @Override
    public Iterable<Serializable> getSuccesses() {
        return null;
    }

    @Override
    public int getFailureCount() {
        return this.failures.size();
    }

    @Override
    public Iterable<Serializable> getFailures() {
        return this.failures;
    }

    @Override
    public Exception getFailureException(final Serializable failure) {
        return null;
    }

    /**
     * 添加失败对象
     *
     * @param failure
     *            添加的失败对象
     */
    public void addFailure(final Serializable failure) {
        this.failures.add(failure);
    }

}
