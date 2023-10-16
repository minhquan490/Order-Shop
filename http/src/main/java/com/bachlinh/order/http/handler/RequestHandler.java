package com.bachlinh.order.http.handler;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.core.exception.http.HttpRequestMethodNotSupportedException;

public interface RequestHandler {
    <T> void setNativeRequest(NativeRequest<T> request);

    <T> void setNativeResponse(NativeResponse<T> response);

    <T> NativeRequest<T> getNativeRequest();

    <T> NativeResponse<T> getNativeResponse();

    <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException;

    void release();
}
