package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.writer.MessageWriter;
import com.bachlinh.order.handler.strategy.ResponseStrategy;
import com.bachlinh.order.handler.strategy.ServletResponseStrategy;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ServletRouter extends AbstractRouter<HttpServletRequest, HttpServletResponse> {
    private final ServletResponseStrategy strategy = ServletResponseStrategy.defaultStrategy();

    public ServletRouter(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected NativeResponse<?> internalHandle(NativeRequest<?> request) {
        try {
            return getRootNode().handleRequest(request, request.getUrl(), request.getRequestMethod());
        } catch (Throwable throwable) {
            if (throwable instanceof Exception exception) {
                return getRootNode().translateException(exception);
            } else {
                return getRootNode().translateError((Error) throwable);
            }
        }
    }

    @Override
    protected NativeRequest<?> registerReq(HttpServletRequest request) {
        return NativeRequest.buildNativeFromServletRequest(request);
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
        MessageWriter messageWriter = MessageWriter.httpMessageWriter(response);
        messageWriter.writeCookies(nativeResponse.getCookies());
        messageWriter.writeHeader(nativeResponse.getHeaders());
        messageWriter.writeHttpStatus(nativeResponse.getStatusCode());
        messageWriter.writeMessage(nativeResponse.getBody());
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
}
