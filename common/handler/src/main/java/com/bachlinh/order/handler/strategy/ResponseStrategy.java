package com.bachlinh.order.handler.strategy;

import com.bachlinh.order.core.http.NativeResponse;

public interface ResponseStrategy<T> {
    void apply(NativeResponse<?> nativeResponse, T actualResponse);
}
