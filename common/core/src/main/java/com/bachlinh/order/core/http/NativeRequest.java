package com.bachlinh.order.core.http;

import com.bachlinh.order.core.http.converter.internal.HttpServletRequestConverter;
import com.bachlinh.order.core.http.converter.spi.RequestConverter;
import com.bachlinh.order.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Getter
public abstract class NativeRequest<T> {
    private static final Map<Class<?>, RequestConverter<?>> converterMap = new ConcurrentHashMap<>();

    private final T request;

    public abstract MultiValueMap<String, String> getUrlQueryParam();

    public abstract MultiValueMap<String, String> getHeaders();

    public abstract NativeCookie[] getCookies();

    public abstract <U> Payload<U> getBody();

    public abstract String getCustomerIp();

    @SuppressWarnings("unchecked")
    public static NativeRequest<?> buildNativeFromServletRequest(HttpServletRequest servletRequest) {
        if (!converterMap.containsKey(HttpServletRequest.class)) {
            RequestConverter<HttpServletRequest> converter = new HttpServletRequestConverter();
            converterMap.put(HttpServletRequest.class, converter);
            return converter.convert(servletRequest);
        }
        RequestConverter<HttpServletRequest> servletConverter = (RequestConverter<HttpServletRequest>) converterMap.get(HttpServletRequest.class);
        return servletConverter.convert(servletRequest);
    }
}
