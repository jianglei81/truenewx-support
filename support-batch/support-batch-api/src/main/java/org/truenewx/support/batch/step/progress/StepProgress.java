package org.truenewx.support.batch.step.progress;

import java.io.Serializable;

import org.truenewx.data.query.Paging;

/**
 * 步骤进度
 *
 * @author jianglei
 * @since JDK 1.7
 */
public abstract class StepProgress implements Serializable {
    /**
     * 未知总数
     */
    public static final int UNKNOWN_TOTAL = Paging.UNKNOWN_TOTAL;

    private static final long serialVersionUID = 5906089186903342120L;

    private int total;

    /**
     *
     * @param total
     *            要处理的对象总数
     */
    public StepProgress(final int total) {
        this.total = total;
    }

    /**
     * 设置总数，只有在已有总数小于0时有效
     *
     * @param total
     *            总数
     */
    public void setTotal(final int total) {
        if (this.total < 0) {
            this.total = total;
        }
    }

    /**
     *
     * @return 要处理的对象总数
     */
    public int getTotal() {
        return this.total;
    }

    /**
     *
     * @return 处理成功数
     */
    public abstract int getSuccessCount();

    /**
     *
     * @return 成功对象清单
     */
    public abstract Iterable<Serializable> getSuccesses();

    /**
     *
     * @return 处理失败数
     */
    public abstract int getFailureCount();

    /**
     *
     * @return 失败对象清单
     */
    public abstract Iterable<Serializable> getFailures();

    /**
     * 获取指定失败对象的失败原因异常
     *
     * @param failure
     *            失败对象
     * @return 失败原因异常
     */
    public abstract Exception getFailureException(Serializable failure);

}
