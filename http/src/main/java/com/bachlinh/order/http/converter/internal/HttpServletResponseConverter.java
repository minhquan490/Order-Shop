package com.bachlinh.order.http.converter.internal;

import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.converter.spi.ResponseConverter;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;

public class HttpServletResponseConverter implements ResponseConverter<HttpServletResponse> {
    private static final ResponseConverter<HttpServletResponse> INSTANCE = new HttpServletResponseConverter();

    private HttpServletResponseConverter() {
    }

    @Override
    public NativeResponse<?> convert(HttpServletResponse message) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        message.getHeaderNames().forEach(name -> headers.put(name, new ArrayList<>(message.getHeaders(name))));
        return NativeResponse.builder().headers(headers).build();
    }

    public static ResponseConverter<HttpServletResponse> getInstance() {
        return INSTANCE;
    }
}
