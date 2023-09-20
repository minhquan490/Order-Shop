package com.bachlinh.order.core.server.jetty;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.ServerCustomizeException;
import jakarta.servlet.MultipartConfigElement;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.eclipse.jetty.http2.parser.WindowRateControl;
import org.eclipse.jetty.http2.server.AbstractHTTP2ServerConnectionFactory;
import org.eclipse.jetty.http3.server.HTTP3ServerConnectionFactory;
import org.eclipse.jetty.http3.server.HTTP3ServerConnector;
import org.eclipse.jetty.server.AcceptRateLimit;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.MultiPartFormDataCompliance;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.compression.DeflaterPool;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;

public class H3JettyServerCustomize implements JettyServerCustomizer {
    private final int serverPort;

    private final String serverAddress;

    private final String activeProfile;

    public H3JettyServerCustomize(int serverPort, String serverAddress, String activeProfile) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.activeProfile = activeProfile;
    }


    @Override
    public void customize(Server server) {
        Environment environment = Environment.getInstance(activeProfile);
        boolean enableH3 = Boolean.parseBoolean(environment.getProperty("server.http3.enable"));
        if (!enableH3) {
            return;
        }
        Handler handler = combineHandlers(server, environment);
        server.setHandler(handler);
        server.getBeans(ServerConnector.class)
                .stream()
                .map(sc -> sc.getConnectionFactory(AbstractHTTP2ServerConnectionFactory.class))
                .filter(Objects::nonNull)
                .forEach(fact -> {
                    fact.setRateControlFactory(new WindowRateControl.Factory(40));
                    fact.getHttpConfiguration().setMultiPartFormDataCompliance(MultiPartFormDataCompliance.RFC7578);
                });
        server.addBean(new AcceptRateLimit(10, 5, TimeUnit.SECONDS, server));
    }

    private Handler combineHandlers(Server server, Environment environment) {
        HTTP3ServerConnectionFactory h3 = new HTTP3ServerConnectionFactory(buildConfig());
        SslContextFactory.Server sslContextFactory = buildSslContextFactory(environment);
        HTTP3ServerConnector connector = createH3Connector(server, sslContextFactory, h3);
        List<Connector> connectors = Stream.of(server.getConnectors()).collect(Collectors.toList());
        connectors.add(connector);
        server.setConnectors(connectors.toArray(Connector[]::new));
        Collection<Handler> handlers = getHandlers(server, environment);
        handlers.add(createRequestLogHandler());
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(handlers.toArray(new Handler[0]));
        var gzipHandler = createGzipHandler(server.getHandler());
        handlerCollection.addHandler(gzipHandler);
        return handlerCollection;
    }

    private Collection<Handler> getHandlers(Server server, Environment environment) {
        Collection<Handler> handlers = new LinkedHashSet<>(Arrays.asList(server.getHandlers()));
        handlers.forEach(handler -> {
            if (handler instanceof ServletHandler servletHandler) {
                for (var servlet : servletHandler.getServlets()) {
                    servlet.getRegistration().setMultipartConfig(createMultipartConfig(environment));
                }
            }
        });
        return handlers;
    }

    private MultipartConfigElement createMultipartConfig(Environment environment) {
        String diskLocation = environment.getProperty("server.resource.file.upload.temp.path");
        String fileSizeThreshold = environment.getProperty("server.resource.file.serve.size");
        String maxFileSize = environment.getProperty("server.resource.file.upload.maxsize");
        return new MultipartConfigElement(diskLocation, Long.parseLong(maxFileSize), Long.parseLong(maxFileSize), Integer.parseInt(fileSizeThreshold));
    }

    private HttpConfiguration buildConfig() {
        HttpConfiguration config = new HttpConfiguration();
        config.setSendServerVersion(false);
        config.setSecureScheme("https");
        config.addCustomizer(new SecureRequestCustomizer());
        config.setMultiPartFormDataCompliance(MultiPartFormDataCompliance.RFC7578);
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

    private Handler createRequestLogHandler() {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(new CustomRequestLog());
        return requestLogHandler;
    }

    private HandlerWrapper createGzipHandler(Handler handler) {
        var gzipHandler = new GzipHandler();
        gzipHandler.setIncludedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.PATCH.name());
        gzipHandler.setIncludedMimeTypes(MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE);
        gzipHandler.setHandler(handler);
        var deflaterPool = new DeflaterPool(2048, Deflater.DEFAULT_COMPRESSION, true);
        gzipHandler.setDeflaterPool(deflaterPool);
        return gzipHandler;
    }
}