package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.writer.MessageWriter;
import com.bachlinh.order.handler.strategy.ResponseStrategy;
import com.bachlinh.order.handler.strategy.ServletResponseStrategy;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        return NativeResponse.builder().build();
    }

    @Override
    protected ResponseStrategy<HttpServletResponse> getStrategy() {
        return strategy;
    }

    @Override
    protected void writeResponse(NativeResponse<?> nativeResponse, HttpServletResponse response) {
        MessageWriter messageWriter = MessageWriter.httpMessageWriter(response);
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
        messageWriter.writeHttpStatus(nativeResponse.getStatusCode());
        messageWriter.writeMessage(nativeResponse.getBody());
    }
}
