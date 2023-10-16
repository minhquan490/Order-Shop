package com.bachlinh.order.handler.interceptor.spi;

import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;

public interface ObjectInterceptor {
    boolean shouldHandle(NativeRequest<?> request, NativeResponse<?> response);

    void afterHandle(NativeRequest<?> request, NativeResponse<?> response);

    void onCompletion(NativeRequest<?> request, NativeResponse<?> response);
}
