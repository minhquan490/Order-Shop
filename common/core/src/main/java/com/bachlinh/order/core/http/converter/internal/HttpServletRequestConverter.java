package com.bachlinh.order.core.http.converter.internal;

import com.bachlinh.order.core.NativeMethodHandleRequestMetadataReader;
import com.bachlinh.order.core.http.HttpServletNativeRequest;
import com.bachlinh.order.core.http.NativeCookie;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.http.converter.spi.NativeCookieConverter;
import com.bachlinh.order.core.http.converter.spi.RequestConverter;
import com.bachlinh.order.core.http.parser.internal.ServletRequestBodyParser;
import com.bachlinh.order.core.http.parser.spi.RequestBodyParser;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HttpServletRequestConverter implements RequestConverter<HttpServletRequest> {
    private final RequestBodyParser<ServletRequest> parser;
    private final NativeCookieConverter<Cookie> nativeCookieConverter;

    public HttpServletRequestConverter() {
        this.parser = new ServletRequestBodyParser();
        this.nativeCookieConverter = NativeCookieConverter.nativeCookieConverter();
    }

    @Override
    public NativeRequest<?> convert(HttpServletRequest request) {
        var nativeRequest = new HttpServletNativeRequest(request);
        nativeRequest.setQueryParams(parseToQueryParams(request));
        nativeRequest.setHeaders(parseToHeaders(request));
        nativeRequest.setCookies(parseToCookies(request));
        NativeMethodHandleRequestMetadataReader requestMetadataReader = NativeMethodHandleRequestMetadataReader.getInstance();
        Class<?> requestType = requestMetadataReader
                .getNativeMethodMetadata(request.getRequestURI())
                .parameterType();
        nativeRequest.setPayload(new Payload<>(parser.parseRequest(request, requestType)));
        nativeRequest.setCustomerIp(request.getRemoteAddr());
        return nativeRequest;
    }

    private MultiValueMap<String, String> parseToQueryParams(HttpServletRequest request) {
        if (request.getQueryString() != null) {
            String queryString = request.getQueryString();
            String[] queryParams = queryString.split("&");
            MultiValueMap<String, String> queryParamMap = new LinkedMultiValueMap<>(queryParams.length);
            for (String queryParam : queryParams) {
                String[] part = queryParam.split("=");
                queryParamMap.putIfAbsent(part[0], Arrays.asList(part[1].contains(",") ? part[1].split(",") : new String[]{part[1]}));
            }
            return queryParamMap;
        } else {
            return new LinkedMultiValueMap<>(0);
        }
    }

    private MultiValueMap<String, String> parseToHeaders(HttpServletRequest request) {
        var servletHeaders = request.getHeaderNames().asIterator();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        while (servletHeaders.hasNext()) {
            String headerName = servletHeaders.next();
            Iterator<String> headerValues = request.getHeaders(headerName).asIterator();
            List<String> values = new ArrayList<>();
            while (headerValues.hasNext()) {
                values.add(headerValues.next());
            }
            headers.putIfAbsent(headerName, values);
        }
        return headers;
    }

    private NativeCookie[] parseToCookies(HttpServletRequest request) {
        List<NativeCookie> nativeCookies = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                nativeCookies.add(nativeCookieConverter.convert(cookie));
            }
            return nativeCookies.toArray(new NativeCookie[0]);
        }
        return new NativeCookie[0];
    }
}
