package com.bachlinh.order.core.http.server.ssl;

import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.incubator.codec.http3.Http3;
import io.netty.incubator.codec.quic.QuicSslContext;
import io.netty.incubator.codec.quic.QuicSslContextBuilder;

import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.http.ssl.spi.SslStoreProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public final class SslContextProvider {
    private final KeyManagerFactory factory;
    private final SslStoreProvider provider;

    public SslContextProvider(String certPath, String keyPath) {
        try {
            this.provider = SslStoreProvider.defaultProvider(certPath, keyPath, "");
            this.factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            this.factory.init(provider.getKeyStore(), provider.getKeyPassword().toCharArray());
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new CriticalException("Can not config ssl", e);
        }
    }


    public SslContext createSslContext() throws SSLException {
        return SslContextBuilder.forServer(factory)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.ALPN, ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE, ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1))
                .build();
    }

    public QuicSslContext createQuicSsl() {
        return QuicSslContextBuilder.forServer(factory, provider.getKeyPassword())
                .applicationProtocols(Http3.supportedApplicationProtocols())
                .build();
    }
}
