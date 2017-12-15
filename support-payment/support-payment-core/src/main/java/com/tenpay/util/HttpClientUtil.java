package com.tenpay.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Http客户端工具类<br/>
 * 这是内部调用类，请不要在外部调用。
 * 
 * @author miklchen
 * 
 */
public class HttpClientUtil {

    public static final String SunX509 = "SunX509";
    public static final String JKS = "JKS";
    public static final String PKCS12 = "PKCS12";
    public static final String TLS = "TLS";

    /**
     * get HttpURLConnection
     * 
     * @param strUrl
     *            url地址
     * @return HttpURLConnection
     * @throws IOException
     */
    public static HttpURLConnection getHttpURLConnection(final String strUrl) throws IOException {
        final URL url = new URL(strUrl);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        return httpURLConnection;
    }

    /**
     * get HttpsURLConnection
     * 
     * @param strUrl
     *            url地址
     * @return HttpsURLConnection
     * @throws IOException
     */
    public static HttpsURLConnection getHttpsURLConnection(final String strUrl) throws IOException {
        final URL url = new URL(strUrl);
        final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        return httpsURLConnection;
    }

    /**
     * 获取不带查询串的url
     * 
     * @param strUrl
     * @return String
     */
    public static String getURL(final String strUrl) {

        if (null != strUrl) {
            final int indexOf = strUrl.indexOf("?");
            if (-1 != indexOf) {
                return strUrl.substring(0, indexOf);
            }

            return strUrl;
        }

        return strUrl;

    }

    /**
     * 获取查询串
     * 
     * @param strUrl
     * @return String
     */
    public static String getQueryString(final String strUrl) {

        if (null != strUrl) {
            final int indexOf = strUrl.indexOf("?");
            if (-1 != indexOf) {
                return strUrl.substring(indexOf + 1, strUrl.length());
            }

            return "";
        }

        return strUrl;
    }

    /**
     * 查询字符串转换成Map<br/>
     * name1=key1&name2=key2&...
     * 
     * @param queryString
     * @return
     */
    public static Map<String, String> queryString2Map(final String queryString) {
        if (null == queryString || "".equals(queryString)) {
            return null;
        }

        final Map<String, String> m = new HashMap<String, String>();
        final String[] strArray = queryString.split("&");
        for (int index = 0; index < strArray.length; index++) {
            final String pair = strArray[index];
            HttpClientUtil.putMapByPair(pair, m);
        }

        return m;

    }

    /**
     * 把键值添加至Map<br/>
     * pair:name=value
     * 
     * @param pair
     *            name=value
     * @param m
     */
    public static void putMapByPair(final String pair, final Map<String, String> m) {

        if (null == pair || "".equals(pair)) {
            return;
        }

        final int indexOf = pair.indexOf("=");
        if (-1 != indexOf) {
            final String k = pair.substring(0, indexOf);
            final String v = pair.substring(indexOf + 1, pair.length());
            if (null != k && !"".equals(k)) {
                m.put(k, v);
            }
        } else {
            m.put(pair, "");
        }
    }

    /**
     * BufferedReader转换成String<br/>
     * 注意:流关闭需要自行处理
     * 
     * @param reader
     * @return String
     * @throws IOException
     */
    public static String bufferedReader2String(final BufferedReader reader) throws IOException {
        final StringBuffer buf = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
            buf.append("\r\n");
        }

        return buf.toString();
    }

    /**
     * 处理输出<br/>
     * 注意:流关闭需要自行处理
     * 
     * @param out
     * @param data
     * @param len
     * @throws IOException
     */
    public static void doOutput(final OutputStream out, final byte[] data, final int len)
            throws IOException {
        int dataLen = data.length;
        int off = 0;
        while (off < dataLen) {
            if (len >= dataLen) {
                out.write(data, off, dataLen);
            } else {
                out.write(data, off, len);
            }

            // 刷新缓冲区
            out.flush();

            off += len;

            dataLen -= len;
        }

    }

    /**
     * 获取SSLContext
     * 
     * @param trustFile
     * @param trustPasswd
     * @param keyFile
     * @param keyPasswd
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    public static SSLContext getSSLContext(final FileInputStream trustFileInputStream,
            final String trustPasswd, final FileInputStream keyFileInputStream,
            final String keyPasswd) throws NoSuchAlgorithmException, KeyStoreException,
            CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {

        // ca
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(HttpClientUtil.SunX509);
        final KeyStore trustKeyStore = KeyStore.getInstance(HttpClientUtil.JKS);
        trustKeyStore.load(trustFileInputStream, HttpClientUtil.str2CharArray(trustPasswd));
        tmf.init(trustKeyStore);

        final char[] kp = HttpClientUtil.str2CharArray(keyPasswd);
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(HttpClientUtil.SunX509);
        final KeyStore ks = KeyStore.getInstance(HttpClientUtil.PKCS12);
        ks.load(keyFileInputStream, kp);
        kmf.init(ks, kp);

        final SecureRandom rand = new SecureRandom();
        final SSLContext ctx = SSLContext.getInstance(HttpClientUtil.TLS);
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), rand);

        return ctx;
    }

    /**
     * 获取CA证书信息
     * 
     * @param cafile
     *            CA证书文件
     * @return Certificate
     * @throws CertificateException
     * @throws IOException
     */
    public static Certificate getCertificate(final File cafile) throws CertificateException,
            IOException {
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final FileInputStream in = new FileInputStream(cafile);
        final Certificate cert = cf.generateCertificate(in);
        in.close();
        return cert;
    }

    /**
     * 字符串转换成char数组
     * 
     * @param str
     * @return char[]
     */
    public static char[] str2CharArray(final String str) {
        if (null == str) {
            return null;
        }

        return str.toCharArray();
    }

    /**
     * 存储ca证书成JKS格式
     * 
     * @param cert
     * @param alias
     * @param password
     * @param out
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public static void storeCACert(final Certificate cert, final String alias,
            final String password, final OutputStream out) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        final KeyStore ks = KeyStore.getInstance("JKS");

        ks.load(null, null);

        ks.setCertificateEntry(alias, cert);

        // store keystore
        ks.store(out, HttpClientUtil.str2CharArray(password));

    }

    public static InputStream String2Inputstream(final String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

}
