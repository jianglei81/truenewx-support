package org.truenewx.support.unstructured.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.support.unstructured.web.resolver.UnstructuredReadUrlResolver;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 格式化日期输出标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnstructuredReadUrlTag extends SimpleTagSupport {

    private String value;
    private boolean thumbnail;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setValue(String value) {
        this.value = value;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    private UnstructuredReadUrlResolver getReadUrlResolver() {
        PageContext pageContext = (PageContext) getJspContext();
        ApplicationContext context = SpringWebUtil.getApplicationContext(pageContext);
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, UnstructuredReadUrlResolver.class);
        }
        return null;
    }

    @Override
    public void doTag() throws JspException, IOException {
        UnstructuredReadUrlResolver readUrlResolver = getReadUrlResolver();
        if (readUrlResolver != null) {
            try {
                String readUrl = readUrlResolver.getReadUrl(this.value, this.thumbnail);
                if (readUrl != null) {
                    JspWriter out = getJspContext().getOut();
                    out.print(readUrl);
                }
            } catch (BusinessException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
    }

}
