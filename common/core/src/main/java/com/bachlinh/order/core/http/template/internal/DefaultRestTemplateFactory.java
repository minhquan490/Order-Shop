package com.bachlinh.order.core.http.template.internal;

import static java.net.http.HttpClient.Redirect.NORMAL;
import com.bachlinh.order.core.http.template.spi.RestTemplate;
import com.bachlinh.order.core.http.template.spi.RestTemplateFactory;

import java.net.CookieManager;
import java.net.http.HttpClient;

public class DefaultRestTemplateFactory implements RestTemplateFactory {
    private final HttpClient.Builder clientBuilder;

    private DefaultRestTemplateFactory() {
        this.clientBuilder = HttpClient.newBuilder();
    }

    @Override
    public RestTemplate create() throws Exception {
        clientBuilder.version(HttpClient.Version.HTTP_2);
        clientBuilder.followRedirects(NORMAL);
        clientBuilder.cookieHandler(new CookieManager());
        return new JavaBaseRestTemplate(clientBuilder.build());
    }

    public static Builder builder() {
        return new DefaultRestTemplateFactoryBuilder();
    }

    private static class DefaultRestTemplateFactoryBuilder implements Builder {

        @Override
        public Builder pemCertificatePath(String path) {
            return this;
        }

        @Override
        public Builder pemCertificateKeyPath(String path) {
            return this;
        }

        @Override
        public Builder pemCertificatePassword(String password) {
            return this;
        }

        @Override
        public RestTemplateFactory build() {
            return new DefaultRestTemplateFactory();
        }
    }
}
