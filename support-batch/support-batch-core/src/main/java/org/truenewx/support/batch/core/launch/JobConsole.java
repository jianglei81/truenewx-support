package org.truenewx.support.batch.core.launch;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nullable;

import org.truenewx.core.tuple.DeviatedNumber;
import org.truenewx.support.batch.core.JobSummary;

/**
 * 作业控制台
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface JobConsole {

    /**
     *
     * @return 作业名称集
     */
    Collection<String> getJobNames();

    /**
     * 启动作业
     *
     * @param jobName
     *            作业名称
     * @param params
     *            参数集
     * @param starter
     *            启动者
     * @return 作业执行id
     */
    Long start(String jobName, Properties params, Serializable starter);

    /**
     * 终止作业执行，终止的作业不可重启
     *
     * @param id
     *            作业执行id
     * @return 是否终止成功
     */
    boolean stop(long id);

    /**
     * 放弃作业执行，放弃的作业可以重启
     *
     * @param id
     *            作业执行id
     */
    void abandon(long id);

    /**
     * 重启作业
     *
     * @param id
     *            作业执行id
     * @return 重启后新的作业执行id
     */
    Long restart(long id);

    /**
     * 获取指定作业执行的总数
     *
     * @param id
     *            作业执行id
     * @param timeout
     *            等待总数计算完成的超时毫秒数
     * @return 总数
     */
    DeviatedNumber<Integer> getTotal(long id, long timeout);

    /**
     * 获取指定作业汇总摘要
     *
     * @param id
     *            作业执行id
     * @return 作业汇总摘要
     */
    JobSummary getSummary(long id);

    /**
     * 判断指定作业执行是否已经结束
     *
     * @param id
     *            作业执行id
     * @return 是否已经结束执行，如果指定作业执行不存在，则返回null
     */
    @Nullable
    Boolean isEnd(long id);

    /**
     * 获取指定启动者启动的运行中的作业数
     *
     * @param starter
     * @return
     *
     * @author jianglei
     */
    int countRunning(Serializable starter);

    /**
     * 分页查询指定启动者的作业执行对象清单
     *
     * @param starter
     *            启动者
     * @return 作业执行对象清单
     */
    List<JobSummary> findRunning(Serializable starter);

}
