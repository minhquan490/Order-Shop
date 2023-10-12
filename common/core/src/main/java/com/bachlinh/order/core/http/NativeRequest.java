package com.bachlinh.order.core.http;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.converter.internal.HttpServletRequestConverter;
import com.bachlinh.order.core.http.converter.spi.RequestConverter;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NativeRequest<T> {
    private static final Map<Class<?>, RequestConverter<?>> converterMap = new ConcurrentHashMap<>();

    private final T request;
    private final String url;
    private final RequestMethod requestMethod;
    private final boolean isMultipart;

    protected NativeRequest(T request, String url, RequestMethod requestMethod, boolean isMultipart) {
        this.request = request;
        this.url = url;
        this.requestMethod = requestMethod;
        this.isMultipart = isMultipart;
    }

    public abstract MultiValueMap<String, String> getUrlQueryParam();

    public abstract MultiValueMap<String, String> getHeaders();

    public abstract <U> Payload<U> getBody();

    public abstract String getCustomerIp();

    public abstract void cleanUp();

    public abstract String getCsrfToken();

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

    public T getRequest() {
        return this.request;
    }

    public String getUrl() {
        return this.url;
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public boolean isMultipart() {
        return this.isMultipart;
    }
}
