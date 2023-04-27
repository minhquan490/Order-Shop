package com.bachlinh.order.core.http.converter.internal;

import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.converter.spi.NativeCookieConverter;
import jakarta.servlet.http.Cookie;

public class ServletNativeCookieConverter implements NativeCookieConverter<Cookie> {
    private static final NativeCookieConverter<Cookie> INSTANCE = new ServletNativeCookieConverter();

    private ServletNativeCookieConverter() {
    }

    @Override
    public NativeCookie convert(Cookie cookie) {
        return new NativeCookie(cookie.getPath(),
                cookie.getMaxAge(),
                cookie.getSecure(),
                cookie.isHttpOnly(),
                cookie.getValue(),
                cookie.getName(),
                cookie.getDomain());
    }

    public static NativeCookieConverter<Cookie> getInstance() {
        return INSTANCE;
    }
}
