package com.bachlinh.order.core.http.converter.internal;

import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.converter.spi.ServletCookieConverter;
import jakarta.servlet.http.Cookie;

public class SpringServletCookieConverter implements ServletCookieConverter<NativeCookie> {
    private static final ServletCookieConverter<NativeCookie> INSTANCE = new SpringServletCookieConverter();

    private SpringServletCookieConverter() {
    }

    @Override
    public Cookie convert(NativeCookie message) {
        Cookie cookie = new Cookie(message.name(), message.value());
        cookie.setDomain(message.domain());
        cookie.setHttpOnly(message.httpOnly());
        cookie.setPath(message.path());
        cookie.setSecure(message.secure());
        cookie.setMaxAge(message.maxAge());
        return cookie;
    }

    public static ServletCookieConverter<NativeCookie> getInstance() {
        return INSTANCE;
    }
}
