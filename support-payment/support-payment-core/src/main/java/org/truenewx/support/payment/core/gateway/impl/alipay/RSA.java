package org.truenewx.support.payment.core.gateway.impl.alipay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

final class RSA {

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * RSA签名
     *
     * @param content
     *            待签名数据
     * @param privateKey
     *            商户私钥
     * @param input_charset
     *            编码格式
     * @return 签名值
     */
    public static String sign(final String content, final String privateKey,
            final String input_charset) {
        try {
            final PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            final KeyFactory keyf = KeyFactory.getInstance("RSA");
            final PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            final java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(input_charset));

            final byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * RSA验签名检查
     *
     * @param content
     *            待签名数据
     * @param sign
     *            签名值
     * @param ali_public_key
     *            支付宝公钥
     * @param input_charset
     *            编码格式
     * @return 布尔值
     */
    public static boolean verify(final String content, final String sign,
            final String ali_public_key, final String input_charset) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final byte[] encodedKey = Base64.decode(ali_public_key);
            final PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            final java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));

            final boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 解密
     *
     * @param content
     *            密文
     * @param private_key
     *            商户私钥
     * @param input_charset
     *            编码格式
     * @return 解密后的字符串
     */
    public static String decrypt(final String content, final String private_key,
            final String input_charset) throws Exception {
        final PrivateKey prikey = getPrivateKey(private_key);

        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, prikey);

        final InputStream ins = new ByteArrayInputStream(Base64.decode(content));
        final ByteArrayOutputStream writer = new ByteArrayOutputStream();
        // rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
        final byte[] buf = new byte[128];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return new String(writer.toByteArray(), input_charset);
    }

    /**
     * 得到私钥
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(final String key) throws Exception {

        byte[] keyBytes;

        keyBytes = Base64.decode(key);

        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        final PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }
}
