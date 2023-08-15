package com.bachlinh.order.utils;

import com.bachlinh.order.utils.map.MultiValueMap;
import com.bachlinh.order.utils.map.MultiValueMapAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Enumeration;
import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderUtils {

    private static final String AUTHORIZE_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "Refresh";
    private static final String CLIENT_SECRET = "client-secret";

    public static String getRequestHeaderValue(String headerName, HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    public static String getAuthorizeHeader(HttpServletRequest request) {
        return getRequestHeaderValue(AUTHORIZE_HEADER, request);
    }

    public static String getRefreshHeader(HttpServletRequest request) {
        return getRequestHeaderValue(REFRESH_HEADER, request);
    }

    public static MultiValueMap<String, Object> getRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        MultiValueMap<String, Object> headers = new MultiValueMapAdapter<>(new HashMap<>());
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            request.getHeaders(headerName).asIterator().forEachRemaining(v -> headers.add(headerName, v));
        }
        return headers;
    }

    public static void setAuthorizeHeader(String token, HttpServletResponse response) {
        response.addHeader(AUTHORIZE_HEADER, token);
    }

    public static String getAuthorizeHeader() {
        return AUTHORIZE_HEADER.toLowerCase();
    }

    public static String getRefreshHeader() {
        return REFRESH_HEADER.toLowerCase();
    }

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }
}
