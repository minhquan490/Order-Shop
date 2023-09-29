package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.container.ContainerWrapper;
import org.springframework.lang.NonNull;

class DefaultControllerManager extends AbstractControllerManager {

    private NativeRequest<?> nativeRequest;
    private NativeResponse<?> nativeResponse;

    DefaultControllerManager(@NonNull String profile, @NonNull ContainerWrapper wrapper) {
        super(profile, wrapper);
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

    @Override
    public void release() {
        nativeRequest = null;
        nativeResponse = null;
    }
}
