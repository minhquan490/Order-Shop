package com.bachlinh.order.handler.controller;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.service.container.ContainerWrapper;

class DefaultControllerManager extends AbstractControllerManager {

    private NativeRequest<?> nativeRequest;
    private NativeResponse<?> nativeResponse;

    DefaultControllerManager(@Nullable ControllerContext controllerContext, @NonNull String profile, @NonNull ContainerWrapper wrapper) {
        super(controllerContext, profile, wrapper);
    }

    @Override
    public <T> void setNativeRequest(NativeRequest<T> request) {
        this.nativeRequest = request;
    }

    @Override
    public <T> void setNativeResponse(NativeResponse<T> response) {
        this.nativeResponse = response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> NativeRequest<T> getNativeRequest() {
        return (NativeRequest<T>) nativeRequest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> NativeResponse<T> getNativeResponse() {
        return (NativeResponse<T>) nativeResponse;
    }
}
