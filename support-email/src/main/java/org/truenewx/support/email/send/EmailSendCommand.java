package org.truenewx.support.email.send;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.truenewx.core.Strings;
import org.truenewx.support.email.EmailMessage;
import org.truenewx.support.email.EmailSource;

/**
 * 邮件发送线程指令
 *
 * @author jianglei
 * @version 1.0.0 2013-9-29
 * @since JDK 1.7
 */
public class EmailSendCommand implements Runnable {
    /**
     * Java邮件发送器
     */
    private JavaMailSender sender;
    /**
     * 邮件源
     */
    private EmailSource source;
    /**
     * 邮件消息集
     */
    private Iterable<EmailMessage> messages;
    /**
     * 发送间隔，单位：毫秒
     */
    private long interval;
    /**
     * 邮件发送进度
     */
    private EmailSendProgress progress;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 构建记录发送进度的指令
     *
     * @param sender
     *            Java邮件发送器
     * @param source
     *            邮件源
     * @param messages
     *            邮件消息清单
     * @param interval
     *            每次发送之间的间隔，单位：毫秒
     * @param progress
     *            TODO
     */
    public EmailSendCommand(final JavaMailSender sender, final EmailSource source,
            final Iterable<EmailMessage> messages, final long interval,
            final EmailSendProgress progress) {
        this.sender = sender;
        this.source = source;
        this.messages = messages;
        this.interval = interval;
        this.progress = progress;
    }

    @Override
    public void run() {
        if (this.progress != null) {
            for (final EmailMessage message : this.messages) {
                // 如果被提前设置为终止则停止发送后续邮件
                if (this.progress.isStop()) {
                    return;
                }
                try {
                    this.logger.info("======= Begin send email to {} =======",
                            StringUtils.join(message.getAddresses(), Strings.COMMA));
                    this.sender.send(new SimpleMimeMessagePreparator(this.source, message));
                    this.progress.addSuccess(message);
                } catch (final Exception e) {
                    e.printStackTrace();
                    this.progress.addFailure(message, e);
                }
                if (this.interval > 0) {
                    try {
                        Thread.sleep(this.interval);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (final EmailMessage message : this.messages) {
                try {
                    this.logger.info("======= Begin send email to {} =======",
                            StringUtils.join(message.getAddresses(), Strings.COMMA));
                    this.sender.send(new SimpleMimeMessagePreparator(this.source, message));
                } catch (final MailException e) {
                    e.printStackTrace(); // 尽量尝试发送所有邮件
                }
            }
        }
    }
}
