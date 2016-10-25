package org.truenewx.support.batch.item;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.truenewx.support.batch.core.step.tasklet.AbstractTasklet;
import org.truenewx.support.batch.core.step.tasklet.ProgressTasklet;

/**
 * 按项目批处理<br/>
 * 该接口仅适合处理无传入参数的场景，一般用于定期遍历数据库记录或特定文件/目录进行处理时，对执行进度不关注<br/>
 * 读取部分通常为有状态的实现，在一次遍历结束之前，即使多线程并发读取，仍然只能依次读到数据，也就意味着同样的处理无法并发执行<br/>
 * 如需传入参数，或者关注执行进度，则需通过继承 {@link AbstractTasklet} 或 {@link ProgressTasklet} 实现
 *
 * @author jianglei
 * @since JDK 1.7
 */
public interface Itemlet<I, O> extends ItemReader<I>, ItemProcessor<I, O>, ItemWriter<O> {

    /**
     * 获取提交间隔数，每处理完指定项目数，即进行一次提交写入动作
     *
     * @return 提交间隔数
     */
    int getCommitInterval();

}
