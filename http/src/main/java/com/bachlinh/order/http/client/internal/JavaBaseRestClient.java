package com.bachlinh.order.http.client.internal;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.http.client.AbstractRestClient;
import com.bachlinh.order.http.converter.spi.JavaBaseResponseConverter;
import com.bachlinh.order.core.utils.JacksonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class JavaBaseRestClient extends AbstractRestClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpClient httpClient;
    private final JavaBaseResponseConverter converter = JavaBaseResponseConverter.defaultConverter();

    JavaBaseRestClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    protected JsonNode sendRequest(RequestInformation information) throws IOException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(buildRequestUrl(information.getUrl(), information.getUriVariables())));
        switch (information.getMethod()) {
            case RequestMethod.GET -> requestBuilder.GET();
            case RequestMethod.PATCH ->
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(JacksonUtils.writeObjectAsString(information.getBody()), StandardCharsets.UTF_8));
            case RequestMethod.POST ->
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.writeObjectAsString(information.getBody()), StandardCharsets.UTF_8));
            case RequestMethod.DELETE -> requestBuilder.DELETE();
            default -> throw new HttpRequestMethodNotSupportedException(STR."Method [\{information.getMethod().name()}] not supported");
        }
        information.getHeaders().forEach((s, strings) -> strings.forEach(value -> requestBuilder.header(s, value)));
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
