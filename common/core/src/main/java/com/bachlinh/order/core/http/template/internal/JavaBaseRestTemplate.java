package com.bachlinh.order.core.http.template.internal;

import com.bachlinh.order.core.http.template.spi.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Map;

class JavaBaseRestTemplate implements RestTemplate {
    private final HttpClient httpClient;

    JavaBaseRestTemplate(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public JsonNode get(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return null;
    }

    @Override
    public JsonNode put(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return null;
    }

    @Override
    public JsonNode post(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return null;
    }

    @Override
    public JsonNode delete(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return null;
    }
}
