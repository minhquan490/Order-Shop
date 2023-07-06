package com.bachlinh.order.handler.interceptor.spi;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;

public sealed interface WebInterceptor permits AbstractInterceptor {
    boolean preHandle(NativeRequest<?> request, NativeResponse<?> response);

    void postHandle(NativeRequest<?> request, NativeResponse<?> response);

    void onComplete(NativeRequest<?> request, NativeResponse<?> response);

    int getOrder();
}
