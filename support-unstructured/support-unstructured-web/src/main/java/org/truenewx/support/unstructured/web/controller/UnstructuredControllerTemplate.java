package org.truenewx.support.unstructured.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    @RequestMapping(value = "/{authorizeType}", method = RequestMethod.POST)
    @HandleableExceptionMessage
    @ResponseBody
    public String upload(final T authorizeType, final MultipartHttpServletRequest request,
            final HttpServletResponse response) throws BusinessException, IOException {
        final MultipartFile mf = request.getFileMap().values().stream().findFirst().orElse(null);
        if (mf != null) {
            final Map<String, Object> result = new HashMap<>();
            final String filename = mf.getOriginalFilename();
            final InputStream in = mf.getInputStream();
            final U user = getUser();
            final String storageUrl = this.service.write(authorizeType, user, filename, in);
            in.close();
            final String readUrl = this.service.getReadUrl(user, storageUrl);
            result.put("storageUrl", storageUrl);
            result.put("readUrl", readUrl);
            response.addHeader("Content-Type", "text/plain;charset=utf-8"); // 解决跨域访问时无法获得返回结果的问题
            return JsonUtil.toJson(result);
        }
        return Strings.EMPTY;
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
