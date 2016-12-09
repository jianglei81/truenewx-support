package org.truenewx.support.sms.send;

import java.util.List;
import java.util.concurrent.Executor;

import org.truenewx.core.util.concurrent.DefaultThreadPoolExecutor;

/**
 * 抽象的短信内容发送器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractSmsContentSender implements SmsContentSender {
    private SmsContentSpliter contentSpliter;
    private Executor executor = new DefaultThreadPoolExecutor(4, 8);

    /**
     *
     * @param contentSpliter
     *            短信内容分割器
     */
    public void setContentSpliter(final SmsContentSpliter contentSpliter) {
        this.contentSpliter = contentSpliter;
    }

    /**
     *
     * @param executor
     *            线程执行器
     */
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public SmsSendResult send(final String content, final int maxCount,
            final String... mobilePhones) {
        final List<String> contents = this.contentSpliter.split(content, maxCount);
        return send(contents, mobilePhones);
    }

    @Override
    public void send(final String content, final int maxCount, final String[] mobilePhones,
            final SmsSendCallback callback) {
        final List<String> contents = this.contentSpliter.split(content, maxCount);
        this.executor.execute(new SendCommand(contents, mobilePhones, callback));
    }

    /**
     * 分成指定条数的内容发送短信
     *
     * @param contents
     *            内容清单，每一个内容为一条短信
     * @param mobilePhones
     *            手机号码清单
     * @return 发送结果
     */
    protected abstract SmsSendResult send(List<String> contents, String... mobilePhones);

    protected class SendCommand implements Runnable {
        private List<String> contents;
        private String[] mobilePhones;
        private SmsSendCallback callback;

        public SendCommand(final List<String> contents, final String[] mobilePhones,
                final SmsSendCallback callback) {
            this.contents = contents;
            this.mobilePhones = mobilePhones;
            this.callback = callback;
        }

        @Override
        public void run() {
            final SmsSendResult result = send(this.contents, this.mobilePhones);
            this.callback.onSmsSent(result);
        }

    }

}
