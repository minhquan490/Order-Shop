package com.bachlinh.order.handler.strategy;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletResponse;

class DefaultServletResponseStrategy implements ServletResponseStrategy {

    @Override
    public void apply(NativeResponse<?> nativeResponse, HttpServletResponse servletResponse) {
        resolveHeader(nativeResponse, servletResponse);
    }

    private void resolveHeader(NativeResponse<?> response, HttpServletResponse servletResponse) {
        MultiValueMap<String, String> headers = response.getHeaders();
        if (headers == null) {
            headers = new LinkedMultiValueMap<>(0);
        }
        headers.forEach((key, values) -> values.forEach(value -> servletResponse.addHeader(key, value)));
    }
}
