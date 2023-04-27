package com.bachlinh.order.core.controller;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;

public interface Controller<T, U> {
    NativeResponse<T> handle(NativeRequest<U> request);

    String getPath();

    RequestMethod getRequestMethod();

    NativeRequest<U> getNativeRequest();

    NativeResponse<T> getNativeResponse();

    void setNativeRequest(NativeRequest<U> request);

    void setNativeResponse(NativeResponse<T> response);
}
