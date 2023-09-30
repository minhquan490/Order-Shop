package com.bachlinh.order.core.http.parser.internal;

import com.bachlinh.order.core.http.MultipartRequest;
import com.bachlinh.order.core.http.parser.spi.RequestBodyParser;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

import java.io.IOException;

public class MultipartServletRequestBodyParser implements RequestBodyParser<AbstractMultipartHttpServletRequest> {
    @Override
    @SuppressWarnings("unchecked")
    public <U> U parseRequest(AbstractMultipartHttpServletRequest request, Class<?> expectedType) {
        MultipartRequest multipartRequest = new MultipartRequest();
        multipartRequest.setChunked(Boolean.parseBoolean(request.getParameter("is-chunked")));
        multipartRequest.setContentLength(Long.parseLong(request.getParameter("content-length")));
        multipartRequest.setFileName(request.getParameter("file-name"));
        multipartRequest.setContentType(request.getParameter("content-type"));
        MultipartFile multipartFile = request.getFile("content");
        try {
            multipartRequest.setBodyContent(multipartFile.getBytes());
        } catch (IOException e) {
            throw new CriticalException("Fail to obtain multipart data", e);
        }
        return (U) multipartFile;
    }
}
