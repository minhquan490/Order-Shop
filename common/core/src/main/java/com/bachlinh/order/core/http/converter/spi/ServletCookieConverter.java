package com.bachlinh.order.core.http.converter.spi;

import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.converter.internal.SpringServletCookieConverter;
import jakarta.servlet.http.Cookie;

public interface ServletCookieConverter<T> extends Converter<Cookie, T> {
    static ServletCookieConverter<NativeCookie> servletCookieConverter() {
        return SpringServletCookieConverter.getInstance();
    }
}
