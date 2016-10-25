package org.truenewx.support.batch.web.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.tuple.DeviatedNumber;
import org.truenewx.support.batch.core.JobSummary;
import org.truenewx.support.batch.core.launch.JobConsole;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

/**
 * JobConsoleController
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RpcController
public class JobConsoleController {
    @Autowired(required = false)
    private JobConsole jobConsole;

    @RpcMethod
    public Collection<String> getJobNames() {
        return this.jobConsole.getJobNames();
    }

    @RpcMethod
    public Long start(final String jobName, final Properties params, final Serializable starter) {
        return this.jobConsole.start(jobName, params, starter);
    }

    @RpcMethod
    public boolean stop(final long id) {
        return this.jobConsole.stop(id);
    }

    @RpcMethod
    public void abandon(final long id) {
        this.jobConsole.abandon(id);
    }

    @RpcMethod
    public Long restart(final long id) {
        return this.jobConsole.restart(id);
    }

    @RpcMethod
    public DeviatedNumber<Integer> getTotal(final long id, final long timeout) {
        return this.jobConsole.getTotal(id, timeout);
    }

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = JobSummary.class,
            excludes = { "uniqueStepProgress", "total", "successCount", "failureCount" })))
    public JobSummary getSummary(final long id) {
        return this.jobConsole.getSummary(id);
    }

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = JobSummary.class,
            includes = { "status", "total", "successCount", "failureCount" })))
    public JobSummary getCount(final long id) {
        return getSummary(id);
    }

    @RpcMethod
    public Boolean isEnd(final long id) {
        return this.jobConsole.isEnd(id);
    }

    @RpcMethod
    public int countRunning(final Serializable starter) {
        return this.jobConsole.countRunning(starter);
    }

    @RpcMethod
    public List<JobSummary> findRunning(final Serializable starter) {
        return this.jobConsole.findRunning(starter);
    }

}
