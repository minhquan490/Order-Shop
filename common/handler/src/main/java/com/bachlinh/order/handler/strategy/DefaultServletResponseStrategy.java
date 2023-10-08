package com.bachlinh.order.handler.strategy;

import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.ServletCookieConverter;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletResponse;

class DefaultServletResponseStrategy implements ServletResponseStrategy {
    private final ServletCookieConverter<NativeCookie> nativeCookieConverter = ServletCookieConverter.servletCookieConverter();

    @Override
    public void apply(NativeResponse<?> nativeResponse, HttpServletResponse servletResponse) {
        resolveHeader(nativeResponse, servletResponse);
        resolveCookie(nativeResponse, servletResponse);
    }

    private void resolveHeader(NativeResponse<?> response, HttpServletResponse servletResponse) {
        MultiValueMap<String, String> headers = response.getHeaders();
        if (headers == null) {
            headers = new LinkedMultiValueMap<>(0);
        }
        headers.forEach((key, values) -> values.forEach(value -> servletResponse.addHeader(key, value)));
    }

    private void resolveCookie(NativeResponse<?> response, HttpServletResponse servletResponse) {
        if (response.getCookies() != null) {
            for (NativeCookie cookie : response.getCookies()) {
                servletResponse.addCookie(nativeCookieConverter.convert(cookie));
            }
        }
    }
}
