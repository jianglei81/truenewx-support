package org.truenewx.support.qrcode.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.Strings;
import org.truenewx.core.util.DateUtil;
import org.truenewx.core.util.IOUtil;
import org.truenewx.web.rpc.server.annotation.RpcController;

import com.alibaba.fastjson.JSON;

/**
 * 二维码控制器
 *
 * @author liuzhiyi
 * @since JDK 1.8
 */
@RpcController("qrCodeController")
public class QrCodeController {

    /**
     * 二维码图片后缀
     */
    private static final String EXTENSION = "png";

    /**
     * 获取二维码文件流
     *
     * @param request
     *            请求
     *
     * @param response
     *            响应
     * @param md5
     *            图片MD5码
     * @return
     */
    @RequestMapping(value = "/qrcode/{md5}", method = RequestMethod.GET)
    @ResponseBody
    public String detail(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable("md5") final String md5) {
        final Map<String, String> result = new HashMap<>();
        FileInputStream input = null;
        try {
            // 读取二维码文件
            final String baseDir = request.getSession().getServletContext()
                    .getRealPath(IOUtil.FILE_SEPARATOR);
            final String dir = IOUtil.FILE_SEPARATOR + md5.substring(0, 1) + IOUtil.FILE_SEPARATOR
                    + md5.substring(1, 2) + IOUtil.FILE_SEPARATOR + md5.substring(2, 3)
                    + IOUtil.FILE_SEPARATOR;
            final File image = new File(baseDir + dir + md5 + Strings.DOT + EXTENSION);

            // 性能优化处理,如果文件已存在并且未发生过改变则直接返回304状态码
            final Date lastModifiedTime = new Date(request.getDateHeader("If-Modified-Since"));
            final Date imageLastModifiedTime = new Date(image.lastModified());
            response.setContentType("image/*");
            response.setDateHeader("Last-Modified", imageLastModifiedTime.getTime());
            if (DateUtil.secondsBetween(imageLastModifiedTime, lastModifiedTime) != 0) {
                input = new FileInputStream(image);
                IOUtils.copy(input, response.getOutputStream());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);// 如果相等则返回304状态码
            }
            response.getOutputStream().flush();// 一定要关闭
        } catch (final IOException e) {
            result.put("errors", e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (final IOException e) {
                }
            }
        }
        return JSON.toJSONString(result);
    }
}
