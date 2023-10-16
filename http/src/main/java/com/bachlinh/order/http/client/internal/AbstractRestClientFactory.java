package com.bachlinh.order.http.client.internal;

import javax.net.ssl.SSLContext;
import com.bachlinh.order.http.client.RestClientFactory;
import com.bachlinh.order.http.server.ssl.SslContextProvider;

public abstract class AbstractRestClientFactory implements RestClientFactory {

    private final SSLContext sslContext;

    protected AbstractRestClientFactory(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    protected SSLContext getSslContext() {
        return sslContext;
    }

    public abstract static class TemplateBuilder implements Builder {

        private String cerPath;
        private String keyPath;
        private String password = "";

        @Override
        public final Builder pemCertificatePath(String path) {
            this.cerPath = path;
            return this;
        }

        @Override
        public final Builder pemCertificateKeyPath(String path) {
            this.keyPath = path;
            return this;
        }

        @Override
        public final Builder pemCertificatePassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public final RestClientFactory build() {
            SslContextProvider provider = new SslContextProvider(cerPath, keyPath, password);
            return doBuild(provider.createJdkSsl());
        }

        protected abstract RestClientFactory doBuild(SSLContext sslContext);
    }
}
