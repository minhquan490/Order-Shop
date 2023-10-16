package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;

public interface NodeHandler {
    <T, U> NativeResponse<T> handleRequest(UrlHolder urlHolder, RequestMethod method, NativeRequest<U> request);
}
