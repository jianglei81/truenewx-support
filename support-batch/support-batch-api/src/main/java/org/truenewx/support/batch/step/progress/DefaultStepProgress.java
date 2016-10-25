package org.truenewx.support.batch.step.progress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 默认的步骤进度，包含成功和失败对象清单，但不包含失败原因异常
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class DefaultStepProgress extends SuccessStepProgress {

    private static final long serialVersionUID = -7304368614272306490L;

    private Collection<Serializable> failures = new ArrayList<>();

    /**
     * @param total
     *            要处理的对象总数
     */
    public DefaultStepProgress(final int total) {
        super(total);
    }

    @Override
    public int getFailureCount() {
        return this.failures.size();
    }

    @Override
    public Iterable<Serializable> getFailures() {
        return this.failures;
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
