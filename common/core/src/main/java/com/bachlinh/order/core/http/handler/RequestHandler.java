package com.bachlinh.order.core.http.handler;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.RequestMethod;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;

public interface RequestHandler {
    <T> void setNativeRequest(NativeRequest<T> request);

    <T> void setNativeResponse(NativeResponse<T> response);

    NativeRequest<?> getNativeRequest();

    NativeResponse<?> getNativeResponse();

    NativeResponse<?> handleRequest(NativeRequest<?> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException;
}
