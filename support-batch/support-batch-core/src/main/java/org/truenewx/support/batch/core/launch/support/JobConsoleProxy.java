package org.truenewx.support.batch.core.launch.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.springframework.util.Assert;
import org.truenewx.core.functor.algorithm.impl.AlgoFirst;
import org.truenewx.core.tuple.DeviatedNumber;
import org.truenewx.core.util.MathUtil;
import org.truenewx.support.batch.core.JobSummary;
import org.truenewx.support.batch.core.launch.JobConsole;

/**
 * 作业控制台代理<br/>
 * 注意：必须在调用{@link JobConsole}的方法前确保包含至少一个代理目标，否则会导致{@link NullPointerException}
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class JobConsoleProxy implements JobConsole {
    private long offset;
    private List<JobConsole> targets;

    public JobConsoleProxy(final JobConsole... targets) {
        this.targets = Arrays.asList(targets);
        this.offset = getOffset(targets.length);
    }

    private int getOffset(final int targetCount) {
        return (int) Math.pow(10, (int) Math.ceil(Math.log10(targetCount)));
    }

    /**
     * 添加一个代理目标作业操作器
     *
     * @param target
     *            代理目标作业操作器
     */
    public void addTarget(final JobConsole target) {
        final int newOffset = getOffset(this.targets.size() + 1);
        Assert.isTrue(this.offset == newOffset); // 新增的代理目标不能使偏移位增加
        this.targets.add(target);
    }

    @Override
    public Collection<String> getJobNames() {
        // 代理的所有作业操作器都应该包含完全一致的作业，取第一个作业操作器的即可
        final JobConsole target = AlgoFirst.visit(this.targets, null);
        return target == null ? new HashSet<>() : target.getJobNames();
    }

    @Override
    public Long start(final String jobName, final Properties params, final Serializable starter) {
        if (this.targets.isEmpty()) {
            return null;
        }
        int index;
        if (params == null || params.isEmpty()) { // 没有参数则随机分配一个代理目标
            index = MathUtil.randomInt(0, this.targets.size());
        } else { // 有参数，则根据参数的哈希值确定代理目标
            index = Math.abs(params.hashCode()) % this.targets.size();
        }
        final JobConsole target = this.targets.get(index);
        Long id = target.start(jobName, params, starter);
        if (id != null) { // 将代理索引作为执行id的末尾加入
            id = id * this.offset + index;
        }
        return id;
    }

    private JobConsole getTarget(final long executionId) {
        if (this.targets.size() > 0 && this.offset > 0) {
            final int index = (int) (executionId % this.offset);
            return this.targets.get(index);
        }
        return null;
    }

    private long getActualExecutionId(final long executionId) {
        return executionId / this.offset;
    }

    @Override
    public boolean stop(final long id) {
        final JobConsole target = getTarget(id);
        if (target == null) {
            return false;
        }
        return target.stop(getActualExecutionId(id));
    }

    @Override
    public void abandon(final long id) {
        final JobConsole target = getTarget(id);
        if (target != null) {
            target.abandon(getActualExecutionId(id));
        }
    }

    @Override
    public Long restart(final long id) {
        if (this.targets.isEmpty()) {
            return null;
        }
        final int index = (int) (id % this.offset);
        final JobConsole target = this.targets.get(index);
        Long newId = target.restart(getActualExecutionId(id));
        if (newId != null) { // 将代理索引作为执行id的末尾加入
            newId = newId * this.offset + index;
        }
        return newId;
    }

    @Override
    public DeviatedNumber<Integer> getTotal(final long id, final long timeout) {
        final JobConsole target = getTarget(id);
        if (target == null) {
            return null;
        }
        return target.getTotal(getActualExecutionId(id), timeout);
    }

    @Override
    public JobSummary getSummary(final long id) {
        final JobConsole target = getTarget(id);
        if (target == null) {
            return null;
        }
        return target.getSummary(getActualExecutionId(id));
    }

    @Override
    public int countRunning(final Serializable starter) {
        int count = 0;
        for (final JobConsole target : this.targets) {
            count += target.countRunning(starter);
        }
        return count;
    }

    @Override
    public List<JobSummary> findRunning(final Serializable starter) {
        final List<JobSummary> list = new ArrayList<>();
        for (final JobConsole target : this.targets) {
            list.addAll(target.findRunning(starter));
        }
        return list;
    }

    @Override
    public Boolean isEnd(final long id) {
        final JobConsole target = getTarget(id);
        if (target == null) {
            return null;
        }
        return target.isEnd(getActualExecutionId(id));
    }

}
