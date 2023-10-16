package com.bachlinh.order.http.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.bachlinh.order.core.enums.RequestMethod;

public abstract class AbstractRestClient implements RestClient {

    @Override
    public final JsonNode get(String url, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return request(url, headers, uriVariables, null, RequestMethod.GET);
    }

    @Override
    public final JsonNode patch(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return request(url, headers, uriVariables, body, RequestMethod.PATCH);
    }

    @Override
    public final JsonNode post(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return request(url, headers, uriVariables, body, RequestMethod.POST);
    }

    @Override
    public final JsonNode delete(String url, Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) throws IOException {
        return request(url, headers, uriVariables, body, RequestMethod.DELETE);
    }

    private JsonNode request(String url, MultiValueMap<String, String> headers, Map<String, ?> uriVariables, Object body, RequestMethod method) throws IOException {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }
        if (uriVariables == null) {
            uriVariables = new LinkedHashMap<>();
        }
        RequestInformation information = new RequestInformation(url, method, body, headers, uriVariables);
        return sendRequest(information);
    }

    protected abstract JsonNode sendRequest(RequestInformation information) throws IOException;

    protected static class RequestInformation {
        private final String url;
        private final RequestMethod method;
        private final @Nullable Object body;
        private final MultiValueMap<String, String> headers;
        private final Map<String, ?> uriVariables;

        private RequestInformation(String url, RequestMethod method, @Nullable Object body, MultiValueMap<String, String> headers, Map<String, ?> uriVariables) {
            this.url = url;
            this.method = method;
            this.body = body;
            this.headers = headers;
            this.uriVariables = uriVariables;
        }

        public Map<String, ?> getUriVariables() {
            return uriVariables;
        }

        public MultiValueMap<String, String> getHeaders() {
            return headers;
        }

        @Nullable
        public Object getBody() {
            return body;
        }

        public RequestMethod getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }
    }
}
