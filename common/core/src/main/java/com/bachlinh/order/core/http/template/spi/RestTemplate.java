package com.bachlinh.order.core.http.template.spi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

/**
 * Convenience {@code RestTemplate} for fetching network resources
 *
 * @author Hoang Minh Quan
 */
public interface RestTemplate {

    /**
     * Execute http get method.
     *
     * @param url          Request url of resource.
     * @param body         The body of request can be null.
     * @param headers      Headers of request can be null.
     * @param uriVariables Query string of url can not be null, use empty map if query string is not available.
     * @return Response from server with {@code JsonNode} format.
     * @throws IOException If problem occur when communicate with remote server.
     */
    JsonNode get(String url, @Nullable MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException;

    /**
     * Execute http put method.
     *
     * @param url          Request url of resource.
     * @param body         The body of request can be null.
     * @param headers      Headers of request can be null.
     * @param uriVariables Query string of url can not be null, use empty map if query string is not available.
     * @return Response from server with {@code JsonNode} format.
     * @throws IOException If problem occur when communicate with remote server.
     */
    JsonNode patch(String url, Object body, @Nullable MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException;

    /**
     * Execute http post method.
     *
     * @param url          Request url of resource.
     * @param body         The body of request can be null.
     * @param headers      Headers of request can be null.
     * @param uriVariables Query string of url can not be null, use empty map if query string is not available.
     * @return Response from server with {@code JsonNode} format.
     * @throws IOException If problem occur when communicate with remote server.
     */
    JsonNode post(String url, Object body, @Nullable MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException;

    /**
     * Execute http delete method.
     *
     * @param url          Request url of resource.
     * @param body         The body of request can be null.
     * @param headers      Headers of request can be null.
     * @param uriVariables Query string of url can not be null, use empty map if query string is not available.
     * @return Response from server with {@code JsonNode} format.
     * @throws IOException If problem occur when communicate with remote server.
     */
    JsonNode delete(String url, @Nullable Object body, @Nullable MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException;
}
