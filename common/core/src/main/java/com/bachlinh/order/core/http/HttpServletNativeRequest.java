package com.bachlinh.order.core.http;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

@Setter
public class HttpServletNativeRequest extends NativeRequest<HttpServletRequest> {
    private MultiValueMap<String, String> queryParams;
    private MultiValueMap<String, String> headers;
    private NativeCookie[] cookies;
    private Payload<?> payload;
    private String customerIp;

    public HttpServletNativeRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public MultiValueMap<String, String> getUrlQueryParam() {
        return queryParams;
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public NativeCookie[] getCookies() {
        return cookies;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Payload<U> getBody() {
        return (Payload<U>) payload;
    }

    @Override
    public String getCustomerIp() {
        return customerIp;
    }
}
