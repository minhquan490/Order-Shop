package com.bachlinh.order.core.http.template.internal;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.converter.spi.JavaBaseResponseConverter;
import com.bachlinh.order.core.http.template.spi.RestTemplate;
import com.bachlinh.order.core.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class JavaBaseRestTemplate implements RestTemplate {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpClient httpClient;
    private final JavaBaseResponseConverter converter = JavaBaseResponseConverter.defaultConverter();

    JavaBaseRestTemplate(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public JsonNode get(String url, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }
        if (uriVariables == null) {
            uriVariables = new LinkedHashMap<>();
        }
        return request(url, RequestMethod.GET, null, headers, uriVariables);
    }

    @Override
    public JsonNode patch(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }
        if (uriVariables == null) {
            uriVariables = new LinkedHashMap<>();
        }
        return request(url, RequestMethod.PATCH, body, headers, uriVariables);
    }

    @Override
    public JsonNode post(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }
        if (uriVariables == null) {
            uriVariables = new LinkedHashMap<>();
        }
        return request(url, RequestMethod.POST, body, headers, uriVariables);
    }

    @Override
    public JsonNode delete(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }
        if (uriVariables == null) {
            uriVariables = new LinkedHashMap<>();
        }
        return request(url, RequestMethod.DELETE, body, headers, uriVariables);
    }

    private JsonNode request(String url, RequestMethod method, @Nullable Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(buildRequestUrl(url, uriVariables)));
        switch (method) {
            case GET -> requestBuilder.GET();
            case PATCH ->
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(JacksonUtils.writeObjectAsString(body), StandardCharsets.UTF_8));
            case POST ->
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.writeObjectAsString(body), StandardCharsets.UTF_8));
            case DELETE -> requestBuilder.DELETE();
        }
        headers.forEach((s, strings) -> strings.forEach(value -> requestBuilder.header(s, value)));
        try {
            HttpResponse<byte[]> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
            return converter.convert(response);
        } catch (InterruptedException e) {
            logger.error("Can not send request !", e);
            return NullNode.getInstance();
        }
    }

    private String buildRequestUrl(String url, Map<String, ?> uriVariables) {
        Collection<String> uriVariablePart = new ArrayList<>();
        uriVariables.forEach((s, o) -> uriVariablePart.add(String.join("=", s, String.valueOf(o))));
        return url + "?" + String.join("&", uriVariablePart.toArray(new String[0]));
    }
}
