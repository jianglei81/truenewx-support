package com.tenpay.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.truenewx.core.Strings;

/**
 * xml工具类
 *
 * @author miklchen
 *
 */
public class XmlUtil {

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Map<String, String> doXmlParse(final String strxml)
            throws JDOMException, IOException {
        if (null == strxml || "".equals(strxml)) {
            return null;
        }

        final Map<String, String> m = new HashMap<>();
        final InputStream in = HttpClientUtil.String2Inputstream(strxml);
        final SAXBuilder builder = new SAXBuilder();
        final Document doc = builder.build(in);
        final Element root = doc.getRootElement();
        final List<?> list = root.getChildren();
        final Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            final Element e = (Element) it.next();
            final String k = e.getName();
            String v = "";
            final List<?> children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = XmlUtil.getChildrenText(children);
            }

            m.put(k, v);
        }

        // 关闭流
        in.close();

        return m;
    }

    /**
     * 获取子结点的xml
     *
     * @param children
     * @return String
     */
    public static String getChildrenText(final List<?> children) {
        final StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            final Iterator<?> it = children.iterator();
            while (it.hasNext()) {
                final Element e = (Element) it.next();
                final String name = e.getName();
                final String value = e.getTextNormalize();
                final List<?> list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(XmlUtil.getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    /**
     * 获取xml编码字符集
     *
     * @param strxml
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public static String getXmlEncoding(final String strxml) throws JDOMException, IOException {
        final InputStream in = HttpClientUtil.String2Inputstream(strxml);
        final SAXBuilder builder = new SAXBuilder();
        final Document doc = builder.build(in);
        in.close();
        return (String) doc.getProperty("encoding");
    }

    /**
     * 获取xml格式字符串
     *
     * @param xmlMap
     * @return
     *
     * @author liuzhiyi
     */
    public static String parseXmlString(final Map<?, ?> xmlMap) {
        if (xmlMap != null && !xmlMap.isEmpty()) {
            final StringBuffer strxml = new StringBuffer("<xml>");
            for (final Object key : xmlMap.keySet()) {
                final Object value = xmlMap.get(key);
                strxml.append("<" + key.toString() + ">");
                strxml.append(value.toString());
                strxml.append("</" + key.toString() + ">");
            }
            strxml.append("</xml>");
            return strxml.toString();
        }
        return Strings.EMPTY;
    }

}
