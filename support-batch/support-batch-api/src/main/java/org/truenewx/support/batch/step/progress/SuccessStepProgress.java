package org.truenewx.support.batch.step.progress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 包含成功对象清单的作业步骤进度
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class SuccessStepProgress extends StepProgress {

    private static final long serialVersionUID = 1525063905366547817L;

    private Collection<Serializable> successes = new ArrayList<>();

    /**
     * @param total
     *            要处理的对象总数
     */
    public SuccessStepProgress(final int total) {
        super(total);
    }

    @Override
    public int getSuccessCount() {
        return this.successes.size();
    }

    @Override
    public Iterable<Serializable> getSuccesses() {
        return this.successes;
    }

    @Override
    public int getFailureCount() {
        return 0;
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
     * 添加成功对象
     *
     * @param success
     *            添加的成功对象
     */
    public void addSuccess(final Serializable success) {
        this.successes.add(success);
    }

}
