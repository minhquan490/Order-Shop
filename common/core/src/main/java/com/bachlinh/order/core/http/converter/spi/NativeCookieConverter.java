package com.bachlinh.order.core.http.converter.spi;

import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.converter.internal.ServletNativeCookieConverter;
import jakarta.servlet.http.Cookie;

public interface NativeCookieConverter<T> extends Converter<NativeCookie, T> {
    static NativeCookieConverter<Cookie> nativeCookieConverter() {
        return new ServletNativeCookieConverter();
    }
}
