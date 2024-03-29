package com.bachlinh.order.http.converter.internal;

import com.bachlinh.order.core.NativeMethodHandleRequestMetadataReader;
import com.bachlinh.order.http.HttpServletNativeRequest;
import com.bachlinh.order.http.MultipartRequest;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.http.converter.spi.RequestConverter;
import com.bachlinh.order.http.parser.internal.MultipartServletRequestBodyParser;
import com.bachlinh.order.http.parser.internal.ServletRequestBodyParser;
import com.bachlinh.order.http.parser.spi.RequestBodyParser;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HttpServletRequestConverter implements RequestConverter<HttpServletRequest> {
    private final RequestBodyParser<ServletRequest> parser;
    private final RequestBodyParser<AbstractMultipartHttpServletRequest> multipartParser;

    public HttpServletRequestConverter() {
        this.parser = new ServletRequestBodyParser();
        this.multipartParser = new MultipartServletRequestBodyParser();
    }

    @Override
    public NativeRequest<?> convert(HttpServletRequest request) {
        var nativeRequest = new HttpServletNativeRequest(request);
        nativeRequest.setQueryParams(parseToQueryParams(request));
        nativeRequest.setHeaders(parseToHeaders(request));
        NativeMethodHandleRequestMetadataReader requestMetadataReader = NativeMethodHandleRequestMetadataReader.getInstance();
        var reader = requestMetadataReader.getNativeMethodMetadata(request.getRequestURI());
        if (reader == null) {
            throw new ResourceNotFoundException("Not found", request.getRequestURI());
        }
        Class<?> requestType = reader.parameterType();
        if (nativeRequest.isMultipart()) {
            var multipartRequest = multipartParser.parseRequest((AbstractMultipartHttpServletRequest) request, MultipartRequest.class);
            nativeRequest.setPayload(new Payload<>(multipartRequest));
        } else {
            nativeRequest.setPayload(new Payload<>(parser.parseRequest(request, requestType)));
        }
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
                if (part.length == 1) {
                    return queryParamMap;
                }
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
}
