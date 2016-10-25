package org.truenewx.support.batch.core;

import java.io.Serializable;
import java.util.Date;

import org.springframework.batch.core.BatchStatus;

/**
 * 批量任务汇总摘要
 *
 * @author jianglei
 * @since JDK 1.7
 */
public abstract class BatchSummary implements Serializable {

    private static final long serialVersionUID = 6261677993388878869L;

    protected BatchStatus status;
    protected Date startTime;
    protected Date endTime;

    public BatchStatus getStatus() {
        return this.status;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }
}
