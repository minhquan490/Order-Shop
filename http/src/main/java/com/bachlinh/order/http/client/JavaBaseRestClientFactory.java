package com.bachlinh.order.http.client;

import static java.net.http.HttpClient.Redirect.NORMAL;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.core.utils.JacksonUtils;
import com.bachlinh.order.http.converter.spi.JavaBaseResponseConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

final class JavaBaseRestClientFactory extends AbstractRestClientFactory {
    private final HttpClient.Builder clientBuilder;

    private JavaBaseRestClientFactory(SSLContext sslContext) {
        super(sslContext);
        this.clientBuilder = HttpClient.newBuilder();
    }

    @Override
    public RestClient create() {
        clientBuilder.version(HttpClient.Version.HTTP_2);
        clientBuilder.followRedirects(NORMAL);
        clientBuilder.cookieHandler(new CookieManager());
        clientBuilder.sslContext(getSslContext());
        return new JavaBaseRestClient(clientBuilder.build());
    }

    static Builder builder() {
        return new DefaultRestTemplateFactoryBuilder();
    }

    private static class DefaultRestTemplateFactoryBuilder extends AbstractRestClientFactory.TemplateBuilder {

        @Override
        protected RestClientFactory doBuild(SSLContext sslContext) {
            return new JavaBaseRestClientFactory(sslContext);
        }
    }

    private static class JavaBaseRestClient extends AbstractRestClient {
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
}
