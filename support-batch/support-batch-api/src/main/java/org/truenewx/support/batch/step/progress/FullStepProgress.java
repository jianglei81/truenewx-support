package org.truenewx.support.batch.step.progress;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 完整的步骤进度，包含成功和失败对象清单，且包含失败原因异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FullStepProgress extends SuccessStepProgress {

    private static final long serialVersionUID = -7886345045396714512L;

    private Map<Serializable, Exception> failureExceptionMap = new HashMap<>();

    /**
     * @param total
     *            要处理的对象总数
     */
    public FullStepProgress(final int total) {
        super(total);
    }

    @Override
    public int getFailureCount() {
        return this.failureExceptionMap.size();
    }

    @Override
    public Iterable<Serializable> getFailures() {
        return this.failureExceptionMap.keySet();
    }

    @Override
    public Exception getFailureException(final Serializable failure) {
        return this.failureExceptionMap.get(failure);
    }

    /**
     * 添加指定失败对象及其原因异常
     *
     * @param failure
     *            失败对象
     * @param e
     *            原因异常
     */
    public void addFailure(final Serializable failure, final Exception e) {
        this.failureExceptionMap.put(failure, e);
    }

}
