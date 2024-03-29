package com.bachlinh.order.handler.interceptor;

import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;

public sealed interface WebInterceptor permits AbstractInterceptor {
    boolean preHandle(NativeRequest<?> request, NativeResponse<?> response);

    void postHandle(NativeRequest<?> request, NativeResponse<?> response);

    void onComplete(NativeRequest<?> request, NativeResponse<?> response);

    int getOrder();

    boolean isEnable();
}
