package org.truenewx.support.email;

import org.truenewx.core.Strings;

/**
 * 邮件源
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class EmailSource {
    /**
     * 发件人地址
     */
    private String address;
    /**
     * 发件人名称
     */
    private String name;
    /**
     * 邮件字符编码
     */
    private String encoding = Strings.DEFAULT_ENCODING;

    /**
     * 用默认的字符编码（{@link Strings#DEFAULT_ENCODING}）构建邮件源
     *
     * @param address
     *            发件人地址
     * @param name
     *            发件人名称
     */
    public EmailSource(final String address, final String name) {
        this.address = address;
        this.name = name;
    }

    /**
     * 构建邮件源
     *
     * @param address
     *            发件人地址
     * @param name
     *            发件人名称
     * @param encoding
     *            邮件字符编码
     */
    public EmailSource(final String address, final String name, final String encoding) {
        this.address = address;
        this.name = name;
        this.encoding = encoding;
    }

    /**
     * @return 发件人地址
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * @return 发件人名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return 邮件字符编码
     */
    public String getEncoding() {
        return this.encoding;
    }

}
