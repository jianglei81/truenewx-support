package org.truenewx.support.email.send;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.javamail.MimeMessagePreparator;
import org.truenewx.support.email.EmailMessage;
import org.truenewx.support.email.EmailSource;

/**
 * 简单的多用消息准备器
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class SimpleMimeMessagePreparator implements MimeMessagePreparator {

    private EmailSource source;
    private EmailMessage message;

    /**
     * @param source
     *            邮件源
     * @param message
     *            邮件消息
     */
    public SimpleMimeMessagePreparator(final EmailSource source, final EmailMessage message) {
        this.source = source;
        this.message = message;
    }

    @Override
    public void prepare(final MimeMessage mimeMessage) throws Exception {
        // 设置发件人
        mimeMessage.setFrom(new InternetAddress(this.source.getAddress(), this.source.getName(),
                this.source.getEncoding()));
        // 设置收件人
        final String[] targetAddresses = this.message.getAddresses();
        final Address[] addresses = new Address[targetAddresses.length];
        for (int i = 0; i < targetAddresses.length; i++) {
            addresses[i] = new InternetAddress(targetAddresses[i]);
        }
        mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
        // 设置标题
        mimeMessage.setSubject(this.message.getTitle(), this.source.getEncoding());
        final MimeMultipart mm = new MimeMultipart("alternative");
        final BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(this.message.getContent(),
                "text/html;charset=" + this.source.getEncoding());
        mm.addBodyPart(bodyPart);
        // 设置内容
        mimeMessage.setContent(mm);
    }

}
