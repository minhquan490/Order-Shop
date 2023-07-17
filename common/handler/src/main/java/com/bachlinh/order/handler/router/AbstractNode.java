package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.ResponseConverter;
import com.bachlinh.order.core.http.translator.internal.JsonStringExceptionTranslator;
import com.bachlinh.order.handler.controller.ControllerContext;
import com.bachlinh.order.handler.controller.ControllerManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNode implements Node, NodeHolder {
    private final ResponseConverter<HttpServletResponse> responseConverter = ResponseConverter.servletResponseConverter();
    private final ControllerManager controllerManager;
    private final JsonStringExceptionTranslator exceptionTranslator;
    private final String name;
    private final Node parent;

    @Override
    @NonNull
    public ResponseConverter<HttpServletResponse> getResponseConverter() {
        return responseConverter;
    }

    @Override
    public ControllerContext getContext() {
        return controllerManager.getContext();
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public <T> NativeResponse<T> getNativeResponse() {
        return controllerManager.getNativeResponse();
    }

    @Override
    public <T> NativeRequest<T> getNativeRequest() {
        return controllerManager.getNativeRequest();
    }

    @Override
    public <T> void setNativeRequest(NativeRequest<T> request) {
        controllerManager.setNativeRequest(request);
    }

    @Override
    public <T> void setNativeResponse(NativeResponse<T> response) {
        controllerManager.setNativeResponse(response);
    }

    protected JsonStringExceptionTranslator exceptionTranslator() {
        return exceptionTranslator;
    }

    protected ControllerManager getControllerManager() {
        return controllerManager;
    }
}