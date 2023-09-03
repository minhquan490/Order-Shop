package com.bachlinh.order.core.http;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

@Setter
public class HttpServletNativeRequest extends NativeRequest<HttpServletRequest> {
    private MultiValueMap<String, String> queryParams;
    private MultiValueMap<String, String> headers;
    private NativeCookie[] cookies;
    private Payload<?> payload;
    private String customerIp;
    private final String csrfToken;

    public HttpServletNativeRequest(HttpServletRequest request) {
        super(request, request.getRequestURI(), RequestMethod.valueOf(request.getMethod().toUpperCase()), isMultipartFile(request));
        String environmentName = Environment.getMainEnvironmentName();
        Environment environment = Environment.getInstance(environmentName);
        this.csrfToken = request.getHeader(environment.getProperty("shop.client.csrf.header.key"));
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

    @Override
    public void cleanUp() {
        payload = null;
    }

    @Override
    public String getCsrfToken() {
        return csrfToken;
    }

    private static boolean isMultipartFile(HttpServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.MULTIPART_FORM_DATA_VALUE);
    }
}
