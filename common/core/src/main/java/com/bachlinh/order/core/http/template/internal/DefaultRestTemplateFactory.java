package com.bachlinh.order.core.http.template.internal;

import com.bachlinh.order.core.http.ssl.spi.SslStoreProvider;
import com.bachlinh.order.core.http.template.spi.RestTemplate;
import com.bachlinh.order.core.http.template.spi.RestTemplateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import static java.net.http.HttpClient.Redirect.NORMAL;

public class DefaultRestTemplateFactory implements RestTemplateFactory {
    private final String pemCertificatePath;
    private final String pemCertificateKeyPath;
    private final String pemCertificatePassword;
    private final HttpClient.Builder clientBuilder;

    private DefaultRestTemplateFactory(String pemCertificatePath, String pemCertificateKeyPath, String pemCertificatePassword) {
        this.pemCertificatePath = pemCertificatePath;
        this.pemCertificateKeyPath = pemCertificateKeyPath;
        this.pemCertificatePassword = pemCertificatePassword;
        this.clientBuilder = HttpClient.newBuilder();
    }

    @Override
    public RestTemplate create() throws Exception {
        SslStoreProvider sslStoreProvider = SslStoreProvider.defaultProvider(pemCertificatePath, pemCertificateKeyPath, pemCertificatePassword);
        SSLContext sslContext = createSslContext(sslStoreProvider);
        clientBuilder.sslContext(sslContext);
        clientBuilder.version(HttpClient.Version.HTTP_2);
        clientBuilder.followRedirects(NORMAL);
        clientBuilder.cookieHandler(new CookieManager());
        return new JavaBaseRestTemplate(clientBuilder.build());
    }

    public static Builder builder() {
        return new DefaultRestTemplateFactoryBuilder();
    }

    private SSLContext createSslContext(SslStoreProvider provider) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(provider.getKeyStore());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("RSA");
        kmf.init(provider.getKeyStore(), provider.getKeyPassword().toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private static class DefaultRestTemplateFactoryBuilder implements Builder {
        private String pemCertificatePath;
        private String pemCertificateKeyPath;
        private String pemCertificatePassword;

        @Override
        public Builder pemCertificatePath(String path) {
            this.pemCertificatePath = path;
            return this;
        }

        @Override
        public Builder pemCertificateKeyPath(String path) {
            this.pemCertificateKeyPath = path;
            return this;
        }

        @Override
        public Builder pemCertificatePassword(String password) {
            this.pemCertificatePassword = password;
            return this;
        }

        @Override
        public RestTemplateFactory build() {
            return new DefaultRestTemplateFactory(pemCertificatePath, pemCertificateKeyPath, pemCertificatePassword);
        }
    }
}
