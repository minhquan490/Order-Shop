package com.bachlinh.order.http.ssl.spi;

import com.bachlinh.order.http.ssl.internal.Provider;

import java.security.KeyStore;
import java.util.Objects;

public interface SslStoreProvider {
    KeyStore getKeyStore();

    KeyStore getTrustStore();

    default String getKeyPassword() {
        return null;
    }

    static SslStoreProvider defaultProvider(String certificatePath, String keyPath, String password) {
        Ssl ssl = Provider.getDefault();
        ssl.setCertificate(certificatePath);
        ssl.setCertificatePrivateKey(keyPath);
        ssl.setKeyPassword(Objects.requireNonNullElse(password, ""));
        return Provider.getDefault(ssl);
    }
}
