package org.truenewx.support.log.data.model;

/**
 * 系统日志行
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SystemLogLine {

    private long pos;
    private String content;

    public SystemLogLine(final long pos, final String content) {
        this.pos = pos;
        this.content = content;
    }

    public long getPos() {
        return this.pos;
    }

    public String getContent() {
        return this.content;
    }

}
