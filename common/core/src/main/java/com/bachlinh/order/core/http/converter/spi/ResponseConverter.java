package com.bachlinh.order.core.http.converter.spi;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.internal.HttpServletResponseConverter;
import jakarta.servlet.http.HttpServletResponse;

public interface ResponseConverter<T> extends Converter<NativeResponse<?>, T> {
    static ResponseConverter<HttpServletResponse> servletResponseConverter() {
        return new HttpServletResponseConverter();
    }
}
