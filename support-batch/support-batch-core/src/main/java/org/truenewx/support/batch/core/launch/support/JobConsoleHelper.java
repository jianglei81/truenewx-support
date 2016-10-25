package org.truenewx.support.batch.core.launch.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.support.batch.core.JobSummary;
import org.truenewx.support.batch.core.launch.JobConsole;

/**
 * 作业控制台协助
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JobConsoleHelper {

    protected JobConsole jobConsole;

    @Autowired
    public final void setJobConsole(final JobConsole jobConsole) {
        this.jobConsole = jobConsole;
    }

    /**
     * 等待指定毫秒后，到作业操作器中获取指定作业汇总摘要
     *
     * @param id
     *            作业执行id
     * @param millis
     *            等待毫秒数
     *
     * @return 作业汇总摘要
     */
    protected final JobSummary wait(final long id, final long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.jobConsole.getSummary(id);
    }

    /**
     * 按指定的间隔毫秒数等待指定作业执行结束
     *
     * @param id
     *            作业执行id
     * @param interval
     *            等待间隔，单位：毫秒
     * @return 结束后的作业汇总摘要
     */
    protected final JobSummary waitUntilEnd(final long id, final long interval) {
        Boolean end = this.jobConsole.isEnd(id);
        if (end == null) {
            return null;
        }
        while (Boolean.FALSE.equals(end)) {
            if (interval > 0) {
                try {
                    Thread.sleep(interval);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            end = this.jobConsole.isEnd(id);
        }
        return this.jobConsole.getSummary(id);
    }
}
