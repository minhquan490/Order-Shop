package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;

public sealed interface Controller<T, U> permits AbstractController {
    NativeResponse<T> handle(NativeRequest<U> request);

    String getPath();

    RequestMethod getRequestMethod();

    NativeRequest<U> getNativeRequest();

    NativeResponse<T> getNativeResponse();

    void setNativeRequest(NativeRequest<U> request);

    void setNativeResponse(NativeResponse<T> response);

    String getName();
}
