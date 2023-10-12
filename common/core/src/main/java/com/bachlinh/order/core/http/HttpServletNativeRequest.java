package com.bachlinh.order.core.http;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class HttpServletNativeRequest extends NativeRequest<HttpServletRequest> {
    private MultiValueMap<String, String> queryParams;
    private MultiValueMap<String, String> headers;
    private Payload<?> payload;
    private String customerIp;
    private final String csrfToken;

    static RequestMethod getMethod(String methodName) {
        RequestMethod method = RequestMethod.of(methodName);
        if (method == null) {
            throw new HttpRequestMethodNotSupportedException(methodName);
        }
        return method;
    }

    public HttpServletNativeRequest(HttpServletRequest request) {
        super(request, request.getRequestURI(), getMethod(request.getMethod()), isMultipartFile(request));
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

    public void setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public void setHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public void setPayload(Payload<?> payload) {
        this.payload = payload;
    }

    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp;
    }
}
