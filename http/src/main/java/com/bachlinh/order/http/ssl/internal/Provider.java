package com.bachlinh.order.http.ssl.internal;

import com.bachlinh.order.http.ssl.spi.Ssl;
import com.bachlinh.order.http.ssl.spi.SslStoreProvider;

public final class Provider {
    private Provider() {
    }

    public static SslStoreProvider getDefault(Ssl ssl) {
        return new CertificateFileSslStoreProvider(ssl);
    }

    public static Ssl getDefault() {
        return new SimpleSsl();
    }
}
