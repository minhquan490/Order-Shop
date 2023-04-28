package com.bachlinh.order.mail.http.ssl;

import com.bachlinh.order.core.http.ssl.internal.CertificateFileSslStoreProvider;
import com.bachlinh.order.core.http.ssl.spi.Ssl;
import com.bachlinh.order.core.http.ssl.spi.SslStoreProvider;
import com.bachlinh.order.exception.system.MailException;
import com.google.api.client.http.HttpTransport;
import com.google.auth.http.HttpTransportFactory;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public final class SslHttpTransportFactory implements HttpTransportFactory {
    private final SslStoreProvider sslStoreProvider;

    public SslHttpTransportFactory(String certificatePemLocation, String privateKeyPemLocation) {
        Ssl ssl = Ssl.simpleInstance();
        ssl.setCertificate(certificatePemLocation);
        ssl.setCertificatePrivateKey(privateKeyPemLocation);
        this.sslStoreProvider = CertificateFileSslStoreProvider.from(ssl);
    }

    @Override
    public HttpTransport create() {
        try {
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(sslStoreProvider.getKeyStore(), sslStoreProvider.getKeyPassword().toCharArray())
                    .build();
            return new SslHttpTransport(sslContext);
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new MailException("Has problem when create HttpTransport", e);
        }
    }
}
