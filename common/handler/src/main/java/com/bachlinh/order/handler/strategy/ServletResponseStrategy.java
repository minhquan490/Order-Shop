package com.bachlinh.order.handler.strategy;

import com.bachlinh.order.http.NativeResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface ServletResponseStrategy extends ResponseStrategy<HttpServletResponse> {

    @Override
    void apply(NativeResponse<?> nativeResponse, HttpServletResponse servletResponse);

    static ServletResponseStrategy defaultStrategy() {
        return new DefaultServletResponseStrategy();
    }
}
