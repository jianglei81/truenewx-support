package org.truenewx.support.unstructured.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.support.unstructured.core.UnstructuredServiceTemplate;
import org.truenewx.support.unstructured.core.model.UnstructuredUploadLimit;
import org.truenewx.support.unstructured.web.model.UploadResult;
import org.truenewx.web.exception.annotation.HandleableExceptionMessage;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;

/**
 * 非结构化存储授权控制器模板<br/>
 * 注意：子类必须用@{@link RpcController}注解标注
 *
 * @author jianglei
 *
 */
public abstract class UnstructuredControllerTemplate<T extends Enum<T>, U> {

    @Autowired
    private UnstructuredServiceTemplate<T, U> service;

    /**
     * 获取在当前方针下，当前用户能上传指定授权类型的文件的最大容量，单位：B<br/>
     * 注意：子类必须覆写该方法，并用@{@link RpcMethod}注解标注
     *
     * @param authorizeType
     *            授权类型
     * @return 当前用户能上传指定授权类型的文件的最大容量
     */
    public UnstructuredUploadLimit getUploadLimit(final T authorizeType) throws BusinessException {
        return this.service.getUploadLimit(authorizeType, getUser());
    }

    // 跨域上传支持
    @RequestMapping(value = "/upload/{authorizeType}", method = RequestMethod.OPTIONS)
    public String upload(final T authorizeType, final HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", Strings.ASTERISK);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.setHeader("Access-Control-Max-Age", "30");
        return null;
    }

    @RequestMapping(value = "/upload/{authorizeType}", method = RequestMethod.POST)
    @HandleableExceptionMessage
    @ResponseBody
    public String upload(final T authorizeType, final MultipartHttpServletRequest request,
            final HttpServletResponse response) throws BusinessException, IOException {
        final List<UploadResult> results = new ArrayList<>();
        for (final MultipartFile mf : request.getFileMap().values()) {
            final String filename = mf.getOriginalFilename();
            final InputStream in = mf.getInputStream();
            final U user = getUser();
            final String storageUrl = this.service.write(authorizeType, user, filename, in);
            in.close();
            final String readUrl = this.service.getReadUrl(user, storageUrl);
            final UploadResult result = new UploadResult(filename, storageUrl, readUrl);
            results.add(result);
        }
        // 跨域上传支持
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.setHeader("Access-Control-Allow-Origin", Strings.ASTERISK);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        return JsonUtil.toJson(results);
    }

    /**
     * 当前用户获取指定内部存储URL对应的外部读取URL<br/>
     * 注意：子类必须覆写该方法，并用@{@link RpcMethod}注解标注
     *
     * @param storageUrl
     *            内部存储URL
     *
     * @return 外部读取URL
     * @throws BusinessException
     *             如果指定用户对指定资源没有读取权限
     */
    public String getReadUrl(final String storageUrl) throws BusinessException {
        return this.service.getReadUrl(getUser(), storageUrl);
    }

    @RequestMapping(value = "/{bucket}/{path}", method = RequestMethod.GET)
    @HandleableExceptionMessage
    public String download(final String bucket, final String path, final HttpServletRequest request,
            final HttpServletResponse response) throws BusinessException, IOException {
        final long modifiedSince = request.getDateHeader("If-Modified-Since");
        final U user = getUser();
        final long modifiedTime = this.service.getLastModifiedTime(user, bucket, path);
        response.setDateHeader("Last-Modified", modifiedTime);
        if (modifiedSince == modifiedTime) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 如果相等则返回表示未修改的状态码
        } else {
            final ServletOutputStream out = response.getOutputStream();
            this.service.read(user, bucket, path, out);
            out.close();
        }
        return null;
    }

    protected abstract U getUser();

}
