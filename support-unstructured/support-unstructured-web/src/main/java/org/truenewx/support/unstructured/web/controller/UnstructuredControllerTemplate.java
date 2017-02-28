package org.truenewx.support.unstructured.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.support.unstructured.core.model.UnstructuredWriteToken;

/**
 * 非结构化存储授权控制器模板
 *
 * @author jianglei
 *
 */
public interface UnstructuredControllerTemplate<T extends Enum<T>> {

    UnstructuredWriteToken authorizePrivateWrite(T authorizeType);

    void authorizePublicRead(T authorizeType, String filename);

    String getOuterUrl(T authorizeType, String innerUrl, String protocol);

    @RequestMapping(value = "/{authorizeType}", method = RequestMethod.POST)
    @ResponseBody
    String upload(T authorizeType,
            @RequestParam(value = "protocol", required = false) String protocol,
            @RequestParam(value = "filename", required = false) String filename,
            MultipartHttpServletRequest request) throws BusinessException;

    @RequestMapping(value = "/{bucket}/{path}", method = RequestMethod.GET)
    String download(String bucket, String path, HttpServletResponse response)
            throws BusinessException;

}
