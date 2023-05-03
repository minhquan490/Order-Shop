package com.bachlinh.order.core.server;

import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.eclipse.jetty.http3.server.HTTP3ServerConnectionFactory;
import org.eclipse.jetty.http3.server.HTTP3ServerConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.ServerCustomizeException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class H3JettyServerCustomize implements JettyServerCustomizer {
    private final int serverPort;

    private final String serverAddress;

    private final String activeProfile;


    @Override
    public void customize(Server server) {
        Environment environment = Environment.getInstance(activeProfile);
        boolean enableH3 = Boolean.parseBoolean(environment.getProperty("server.http3.enable"));
        if (!enableH3) {
            return;
        }
        HTTP3ServerConnectionFactory h3 = new HTTP3ServerConnectionFactory(buildConfig());
        SslContextFactory.Server sslContextFactory = buildSslContextFactory(environment);
        HTTP3ServerConnector connector = createH3Connector(server, sslContextFactory, h3);
        List<Connector> connectors = Stream.of(server.getConnectors()).collect(Collectors.toList());
        connectors.add(connector);
        server.setConnectors(connectors.toArray(Connector[]::new));
    }

    private HttpConfiguration buildConfig() {
        HttpConfiguration config = new HttpConfiguration();
        config.setSendServerVersion(false);
        config.setSecureScheme("https");
        config.addCustomizer(new SecureRequestCustomizer());
        return config;
    }

    private SslContextFactory.Server buildSslContextFactory(Environment environment) {
        String h3KeyStore = environment.getProperty("server.ssl.key-store");
        String keyManagerPass = environment.getProperty("server.ssl.key-manager-password");
        String h3KeyStorePass = environment.getProperty("server.ssl.key-store-password");
        String h3KeyStoreType = environment.getProperty("server.ssl.key-store-type");
        String alias = environment.getProperty("server.ssl.keyAlias");
        String protocol = environment.getProperty("server.ssl.protocol");
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setHostnameVerifier(new DefaultHostnameVerifier(getDefaultInstance()));
        sslContextFactory.setEndpointIdentificationAlgorithm("https");
        try {
            sslContextFactory.setKeyStoreResource(Resource.newResource(h3KeyStore));
            sslContextFactory.setKeyStorePassword(h3KeyStorePass);
            sslContextFactory.setKeyStoreType(h3KeyStoreType);
            sslContextFactory.setKeyManagerPassword(keyManagerPass);
            sslContextFactory.setProtocol(protocol);
            sslContextFactory.setCertAlias(alias);
        } catch (IOException e) {
            throw new ServerCustomizeException("Can not config h3 server for use", e);
        }
        return sslContextFactory;
    }

    private PublicSuffixMatcher getDefaultInstance() {
        return new PublicSuffixMatcher(DomainType.ICANN, Arrays.asList("com", "vn", "com.vn"), null);
    }

    private HTTP3ServerConnector createH3Connector(Server server, SslContextFactory.Server sslContextFactory, HTTP3ServerConnectionFactory h3) {
        HTTP3ServerConnector connector = new HTTP3ServerConnector(server, sslContextFactory, h3);
        connector.setHost(serverAddress);
        connector.setPort(serverPort);
        return connector;
    }
}