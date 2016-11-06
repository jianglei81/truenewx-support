package org.truenewx.support.qrcode.tag;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.encrypt.Md5Encrypter;
import org.truenewx.core.util.IOUtil;
import org.truenewx.web.tagext.UiTagSupport;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码显示标签
 *
 * @author liuzhiyi
 * @since JDK 1.8
 */
public class QrCodeTag extends UiTagSupport {

    /**
     * 二维码图片后缀
     */
    private static final String EXTENSION = "png";

    /**
     * 访问地址
     */
    private static final String URL = "qrcode";

    /**
     * 前缀
     */
    private String context;

    /**
     * 二维码值
     */
    private String value;

    /**
     * 二维码大小
     */
    private int size;

    /**
     * LOGO
     */
    private String logo;

    /**
     * @param context
     *            前缀
     *
     * @author liuzhiyi
     */
    public void setContext(final String context) {
        this.context = context;
    }

    /**
     * @param value
     *            二维码值
     *
     * @author liuzhiyi
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @param size
     *            二维码大小
     *
     * @author liuzhiyi
     */
    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * @return LOGO
     *
     * @author liuzhiyi
     */
    public String getLogo() {
        return this.logo;
    }

    /**
     * @param logo
     *            LOGO
     *
     * @author liuzhiyi
     */
    public void setLogo(final String logo) {
        this.logo = logo;
    }

    @Override
    public void doTag() throws JspException, IOException {
        try {
            // 读取保存路径
            final String baseDir = getPageContext().getSession().getServletContext()
                    .getRealPath(IOUtil.FILE_SEPARATOR);
            final String md5 = Md5Encrypter.encrypt32(this.value);
            final String dir = IOUtil.FILE_SEPARATOR + md5.substring(0, 1) + IOUtil.FILE_SEPARATOR
                    + md5.substring(1, 2) + IOUtil.FILE_SEPARATOR + md5.substring(2, 3)
                    + IOUtil.FILE_SEPARATOR;

            // 产生二维码资源
            final Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, Strings.DEFAULT_ENCODING);
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(this.value, BarcodeFormat.QR_CODE,
                    this.size, this.size, hints);

            bitMatrix = updateBit(bitMatrix, 0);
            // 将二维码转换为BufferedImage
            BufferedImage image = toBufferedImage(bitMatrix);
            image = IOUtil.zoomImage(image, this.size);
            // 载入logo
            if (StringUtils.isNotEmpty(this.logo)) {
                Image logoImage;
                if (this.logo.startsWith("http")) {
                    final URL url = new URL(this.logo);
                    final InputStream is = url.openConnection().getInputStream();
                    logoImage = ImageIO.read(is);
                } else {
                    logoImage = ImageIO.read(new File(baseDir + this.logo));
                }
                final Graphics2D gs = image.createGraphics();
                final int logoWidth = logoImage.getWidth(null);
                final int logoHeight = logoImage.getHeight(null);
                final int logoX = (this.size - logoWidth) / 2;
                final int logoY = (this.size - logoHeight) / 2;
                gs.drawImage(logoImage, logoX, logoY, null);
                gs.dispose();
                logoImage.flush();
            }

            // 验证文件夹是否存在
            final File outputDir = new File(baseDir + dir);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 保存二维码图片
            final File outputFile = new File(baseDir + dir + md5 + Strings.DOT + EXTENSION);
            ImageIO.write(image, EXTENSION, outputFile);

            // 输出标签
            print("<img");
            print(" src=\"", this.context + Strings.SLASH + URL + Strings.SLASH + md5, "\"");
            print(joinAttributes());
            print("/>", Strings.ENTER);
        } catch (final IOException | WriterException e) {
            throw new JspException(e);
        }
    }

    /**
     * 将二维码转换为BufferedImage
     *
     * @param matrix
     *            二维码资源
     * @return
     *
     * @author liuzhiyi
     */
    private BufferedImage toBufferedImage(final BitMatrix matrix) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

    private BitMatrix updateBit(final BitMatrix matrix, final int margin) {
        final int tempM = margin * 2;
        final int[] rec = matrix.getEnclosingRectangle(); // 获取二维码图案的属性
        final int resWidth = rec[2] + tempM;
        final int resHeight = rec[3] + tempM;
        final BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for (int i = margin; i < resWidth - margin; i++) { // 循环，将二维码图案绘制到新的bitMatrix中
            for (int j = margin; j < resHeight - margin; j++) {
                if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

}
