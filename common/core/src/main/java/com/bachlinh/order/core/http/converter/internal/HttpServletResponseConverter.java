package com.bachlinh.order.core.http.converter.internal;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.ResponseConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;

public class HttpServletResponseConverter implements ResponseConverter<HttpServletResponse> {
    @Override
    public NativeResponse<?> convert(HttpServletResponse message) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        message.getHeaderNames().forEach(name -> headers.put(name, new ArrayList<>(message.getHeaders(name))));
        return NativeResponse.builder().headers(headers).build();
    }
}
