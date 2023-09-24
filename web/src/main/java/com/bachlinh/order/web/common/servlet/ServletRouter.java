package com.bachlinh.order.web.common.servlet;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.writer.MessageWriter;
import com.bachlinh.order.handler.router.AbstractRouter;
import com.bachlinh.order.handler.strategy.ResponseStrategy;
import com.bachlinh.order.handler.strategy.ServletResponseStrategy;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.WebUtils;

public class ServletRouter extends AbstractRouter<HttpServletRequest, HttpServletResponse> {
    private final ServletResponseStrategy strategy = ServletResponseStrategy.defaultStrategy();
    private final MultipartResolver multipartResolver = new StandardServletMultipartResolver();

    public ServletRouter(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected NativeResponse<?> internalHandle(NativeRequest<?> request) {
        return getRootNode().handleRequest(request, request.getUrl(), request.getRequestMethod());
    }

    @NonNull
    @Override
    protected NativeRequest<?> registerReq(HttpServletRequest request) {
        HttpServletRequest processedRequest;
        processedRequest = checkMultipart(request);
        return NativeRequest.buildNativeFromServletRequest(processedRequest);
    }

    @Override
    protected NativeResponse<?> createDefault() {
        return NativeResponse.builder().headers(new LinkedMultiValueMap<>()).build();
    }

    @Override
    protected ResponseStrategy<HttpServletResponse> getStrategy() {
        return strategy;
    }

    @Override
    protected void writeResponse(NativeResponse<?> nativeResponse, HttpServletResponse response) {
        MessageWriter messageWriter = MessageWriter.httpMessageWriter(response);
        messageWriter.writeCookies(nativeResponse.getCookies());
        messageWriter.writeHeader(nativeResponse.getHeaders());
        messageWriter.writeHttpStatus(nativeResponse.getStatusCode());
        messageWriter.writeMessage(nativeResponse.getBody());
    }

    @Override
    protected void onErrorBeforeHandle(Throwable throwable, HttpServletResponse response) {
        NativeResponse<?> nativeResponse;
        if (throwable instanceof Exception exception) {
            nativeResponse = getRootNode().translateException(exception);
        } else {
            nativeResponse = getRootNode().translateError((Error) throwable);
        }
        writeResponse(nativeResponse, response);
    }

    @Override
    protected void configResponse(NativeResponse<?> nativeResponse, HttpServletResponse actualResponse) {
        var headerNames = actualResponse.getHeaderNames();
        for (var headerName : headerNames) {
            var headerValues = actualResponse.getHeaders(headerName);
            headerValues.forEach(val -> nativeResponse.addHeader(headerName, val));
        }
        nativeResponse.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (nativeResponse.getCookies() != null) {
            for (var cookie : nativeResponse.getCookies()) {
                actualResponse.addCookie(cookie.toServletCookie());
            }
        }
    }

    @Override
    protected void cleanUpRequest(HttpServletRequest actualRequest, NativeRequest<?> transferredRequest) {
        if (transferredRequest != null && transferredRequest.isMultipart()) {
            transferredRequest.cleanUp();
            cleanupMultipart(actualRequest);
        }
    }

    private HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver.isMultipart(request)) {
            return this.multipartResolver.resolveMultipart(request);
        }
        return request;
    }

    private void cleanupMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (multipartRequest != null) {
            this.multipartResolver.cleanupMultipart(multipartRequest);
        }
    }
}
