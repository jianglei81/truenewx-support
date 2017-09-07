package org.truenewx.support.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件提供者抽象实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractEmailProvider implements EmailProvider {
    /**
     * 邮件类型
     */
    private String type;
    /**
     * 邮件标题
     */
    protected String title;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final String getType() {
        return this.type;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
