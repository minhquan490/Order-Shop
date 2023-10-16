package com.bachlinh.order.http.converter.spi;

import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.converter.internal.HttpServletResponseConverter;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseConverter<T> extends Converter<NativeResponse<?>, T> {
    static ResponseConverter<HttpServletResponse> servletResponseConverter() {
        return HttpServletResponseConverter.getInstance();
    }
}
