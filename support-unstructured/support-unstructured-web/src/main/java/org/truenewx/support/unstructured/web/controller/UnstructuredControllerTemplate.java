package org.truenewx.support.unstructured.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.spring.util.PlaceholderResolver;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.support.unstructured.core.UnstructuredServiceTemplate;
import org.truenewx.support.unstructured.core.model.UnstructuredReadMetadata;
import org.truenewx.support.unstructured.core.model.UnstructuredUploadLimit;
import org.truenewx.support.unstructured.web.model.UploadResult;
import org.truenewx.support.unstructured.web.resolver.UnstructuredReadUrlResolver;
import org.truenewx.web.exception.annotation.HandleableExceptionMessage;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.security.annotation.Accessibility;
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.util.WebUtil;

import com.aliyun.oss.internal.Mimetypes;

/**
 * 非结构化存储授权控制器模板<br/>
 * 注意：子类必须用@{@link RpcController}注解标注
 *
 * @author jianglei
 *
 */
public abstract class UnstructuredControllerTemplate<T extends Enum<T>, U>
        implements UnstructuredReadUrlResolver {

    @Autowired
    private UnstructuredServiceTemplate<T, U> service;
    @Autowired
    private PlaceholderResolver placeholderResolver;

    /**
     * 获取在当前方针下，当前用户能上传指定授权类型的文件的最大容量，单位：B<br/>
     * 注意：因模板方法中无法确定授权枚举类型，故需要子类覆写该方法，由于覆写方法不能继承注解，故同时需要使用{@link RpcMethod}注解进行标注
     *
     * @param authorizeType 授权类型
     * @return 当前用户能上传指定授权类型的文件的最大容量
     */
    public UnstructuredUploadLimit getUploadLimit(T authorizeType) throws BusinessException {
        return this.service.getUploadLimit(authorizeType, getUser());
    }

    // 跨域上传支持
    @RequestMapping(value = "/upload/{authorizeType}", method = RequestMethod.OPTIONS)
    public String upload(@PathVariable("authorizeType") T authorizeType,
            HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", Strings.ASTERISK);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.setHeader("Access-Control-Max-Age", "30");
        return null;
    }

    @RequestMapping(value = "/upload/{authorizeType}", method = RequestMethod.POST)
    @HandleableExceptionMessage(respondErrorStatus = false)
    @ResponseBody
    public String upload(@PathVariable("authorizeType") T authorizeType, HttpServletRequest request,
            HttpServletResponse response)
            throws BusinessException, IOException, FileUploadException {
        List<UploadResult> results = new ArrayList<>();
        FileItemFactory fileItemFactory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
        servletFileUpload.setHeaderEncoding(Strings.ENCODING_UTF8);
        List<FileItem> fileItems = servletFileUpload.parseRequest(request);
        for (FileItem fileItem : fileItems) {
            if (!fileItem.isFormField()) {
                String filename = fileItem.getName();
                InputStream in = fileItem.getInputStream();
                // 注意：此处获得的输入流大小与原始文件的大小可能不相同，可能变大或变小
                U user = getUser();
                String storageUrl = this.service.write(authorizeType, user, filename, in);
                in.close();
                String readUrl = this.service.getReadUrl(user, storageUrl, false);
                readUrl = getFullReadUrl(readUrl);
                UploadResult result = new UploadResult(filename, storageUrl, readUrl);
                results.add(result);
            }
        }
        // 跨域上传支持
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.setHeader("Access-Control-Allow-Origin", Strings.ASTERISK);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        return JsonUtil.toJson(results);
    }

    @Override
    public String getReadUrl(String storageUrl, boolean thumbnail) throws BusinessException {
        if (StringUtils.isNotBlank(storageUrl)) {
            String readUrl = this.service.getReadUrl(getUser(), storageUrl, thumbnail);
            return getFullReadUrl(readUrl);
        }
        return null;
    }

    private String getFullReadUrl(String readUrl) {
        // 读取地址以/开头但不以//开头，则视为相对地址，相对地址需考虑添加主机地址和上下文根
        if (readUrl != null && readUrl.length() > 1 && readUrl.startsWith(Strings.SLASH)
                && readUrl.charAt(1) != '/') {
            // 先加上上下文根路径
            String contextPath = SpringWebContext.getRequest().getContextPath();
            if (!contextPath.equals(Strings.SLASH)) {
                readUrl = contextPath + readUrl;
            }
            // 再加上主机地址
            String host = getHost();
            if (host != null) {
                String requestUrl = SpringWebContext.getRequest().getRequestURL().toString();
                // 如果配置的主机地址以//开头，说明允许各种访问协议，此时需去掉请求地址中的协议部分再进行比较
                if (host.startsWith("//")) {
                    int index = requestUrl.indexOf("://");
                    requestUrl = requestUrl.substring(index + 1); // 让请求地址也以//开头
                }
                // 当前请求地址与非结构化存储的外部读取主机地址不一致，则需要将主机地址加入读取地址中
                if (!requestUrl.startsWith(host)) {
                    readUrl = host + readUrl;
                }
            }
        }
        return readUrl;
    }

    protected String getHost() {
        return this.placeholderResolver.resolvePlaceholder(getHostPlaceholderKey());
    }

    protected String getHostPlaceholderKey() {
        return "context.host.unstructured";
    }

    /**
     * 当前用户获取指定内部存储URL集对应的资源读取元信息集<br/>
     *
     * @param storageUrls 内部存储URL集
     * @return 资源读取元信息集
     * @throws BusinessException 如果指定用户对某个资源没有读取权限
     */
    @RpcMethod
    @Accessibility(anonymous = true) // 默认匿名可获取，用户读取权限控制由各方针决定
    public UnstructuredReadMetadata[] getReadMetadatas(String[] storageUrls)
            throws BusinessException {
        UnstructuredReadMetadata[] metadatas = new UnstructuredReadMetadata[storageUrls.length];
        for (int i = 0; i < storageUrls.length; i++) {
            metadatas[i] = this.service.getReadMetadata(getUser(), storageUrls[i]);
            if (metadatas[i] != null) {
                String readUrl = metadatas[i].getReadUrl();
                readUrl = getFullReadUrl(readUrl);
                metadatas[i].setReadUrl(readUrl);
            }
        }
        return metadatas;
    }

    @RequestMapping(value = "/dl/**", method = RequestMethod.GET)
    @Accessibility(anonymous = true) // 默认匿名可下载，用户读取权限控制由各方针决定
    public String download(HttpServletRequest request, HttpServletResponse response)
            throws BusinessException, IOException {
        String url = getBucketAndPathFragmentUrl(request);
        int index = url.indexOf(Strings.SLASH);
        String bucket = url.substring(0, index);
        String path = url.substring(index + 1);

        long modifiedSince = request.getDateHeader("If-Modified-Since");
        U user = getUser();
        long modifiedTime = this.service.getLastModifiedTime(user, bucket, path);
        response.setDateHeader("Last-Modified", modifiedTime);
        response.setContentType(Mimetypes.getInstance().getMimetype(path));
        if (modifiedSince == modifiedTime) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 如果相等则返回表示未修改的状态码
        } else {
            ServletOutputStream out = response.getOutputStream();
            this.service.read(user, bucket, path, out);
            out.close();
        }
        return null;
    }

    /**
     * 获取存储桶和路径所在的URL片段，子类可覆写实现自定义的路径格式
     *
     * @param request HTTP请求
     * @return 存储桶和路径所在的URL片段
     */
    protected String getBucketAndPathFragmentUrl(HttpServletRequest request) {
        String url = WebUtil.getRelativeRequestUrl(request);
        try {
            url = URLDecoder.decode(url, Strings.ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            // 可以保证字符集不会有错
        }
        int index = url.indexOf("/dl/");
        return url.substring(index + 4); // 通配符部分
    }

    protected abstract U getUser();

}
