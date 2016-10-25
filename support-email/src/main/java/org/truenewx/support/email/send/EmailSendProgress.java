package org.truenewx.support.email.send;

import org.truenewx.support.batch.step.progress.FullStepProgress;

/**
 * 邮件发送进度
 *
 * @author jianglei
 * @version 1.0.0 2013-8-7
 * @since JDK 1.8
 */
public class EmailSendProgress extends FullStepProgress {

    private static final long serialVersionUID = 6339119160936381551L;

    /**
     * 是否中途终止
     */
    private boolean stop;

    /**
     * @param total
     *            需发送的邮件总数
     */
    public EmailSendProgress(final int total) {
        super(total);
    }

    /**
     * 判断邮件是否全部发送成功
     *
     * @return true if 邮件全部发送成功, otherwise false
     */
    public boolean isAllSuccess() {
        return getFailureCount() == 0;
    }

    /**
     * 判断邮件是否全部发送失败
     *
     * @return true if 邮件全部发送失败, otherwise false
     */
    public boolean isAllFailure() {
        return getSuccessCount() == 0;
    }

    /**
     * @return 是否中途终止
     */
    public boolean isStop() {
        return this.stop;
    }

    /**
     * 终止发送
     */
    public void stop() {
        this.stop = true;
    }
}
