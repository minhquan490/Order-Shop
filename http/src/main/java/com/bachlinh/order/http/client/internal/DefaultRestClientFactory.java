package com.bachlinh.order.http.client.internal;

import static java.net.http.HttpClient.Redirect.NORMAL;
import com.bachlinh.order.http.client.RestClient;
import com.bachlinh.order.http.client.RestClientFactory;

import java.net.CookieManager;
import java.net.http.HttpClient;

import javax.net.ssl.SSLContext;

public class DefaultRestClientFactory extends AbstractRestClientFactory {
    private final HttpClient.Builder clientBuilder;

    private DefaultRestClientFactory(SSLContext sslContext) {
        super(sslContext);
        this.clientBuilder = HttpClient.newBuilder();
    }

    @Override
    public RestClient create() throws Exception {
        clientBuilder.version(HttpClient.Version.HTTP_2);
        clientBuilder.followRedirects(NORMAL);
        clientBuilder.cookieHandler(new CookieManager());
        clientBuilder.sslContext(getSslContext());
        return new JavaBaseRestClient(clientBuilder.build());
    }

    public static Builder builder() {
        return new DefaultRestTemplateFactoryBuilder();
    }

    private static class DefaultRestTemplateFactoryBuilder extends AbstractRestClientFactory.TemplateBuilder {

        @Override
        protected RestClientFactory doBuild(SSLContext sslContext) {
            return new DefaultRestClientFactory(sslContext);
        }
    }
}
