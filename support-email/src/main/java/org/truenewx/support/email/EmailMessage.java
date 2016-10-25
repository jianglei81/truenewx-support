package org.truenewx.support.email;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 邮件消息
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = 8622432486119864365L;

    /**
     * 收件人地址清单
     */
    private String[] addresses;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;

    /**
     * 构建邮件消息
     *
     * @param addresses
     *            收件人地址清单
     * @param title
     *            标题
     * @param content
     *            内容
     */
    public EmailMessage(final String[] addresses, final String title, final String content) {
        this.addresses = addresses;
        this.title = title;
        this.content = content;
    }

    /**
     * 构建邮件消息
     *
     * @param address
     *            单个收件人地址
     * @param title
     *            标题
     * @param content
     *            内容
     */
    public EmailMessage(final String address, final String title, final String content) {
        this.addresses = new String[] { address };
        this.title = title;
        this.content = content;
    }

    /**
     * @return 收件人地址清单
     */
    public String[] getAddresses() {
        return this.addresses;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return 内容
     */
    public String getContent() {
        return this.content;
    }

    @Override
    public int hashCode() {
        final Object[] array = ArrayUtils.addAll(this.addresses, this.title, this.content);
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EmailMessage other = (EmailMessage) obj;
        return PredEqual.INSTANCE.apply(this.addresses, other.addresses)
                && PredEqual.INSTANCE.apply(this.title, other.title)
                && PredEqual.INSTANCE.apply(this.content, other.content);
    }
}
