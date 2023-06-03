package com.bachlinh.order.handler.strategy;

import jakarta.servlet.http.HttpServletResponse;
import com.bachlinh.order.core.http.NativeResponse;

public interface ServletResponseStrategy {
    void apply(NativeResponse<?> nativeResponse, HttpServletResponse servletResponse);

    static ServletResponseStrategy defaultStrategy() {
        return new DefaultServletResponseStrategy();
    }
}
