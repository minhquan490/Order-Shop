package com.bachlinh.order.core.server.netty;

import com.bachlinh.order.core.server.netty.channel.http2.Http2ServerInitializer;
import com.bachlinh.order.core.server.netty.channel.http3.Http3ServerInitializer;
import com.bachlinh.order.core.server.netty.ssl.SslContextProvider;
import com.bachlinh.order.service.container.DependenciesResolver;
import io.netty.channel.epoll.EpollEventLoopGroup;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadFactory;

public class SimpleNettyServerFactory implements NettyServerFactory {
    private final String certPath;
    private final String keyPath;
    private final DependenciesResolver resolver;
    private final ThreadFactory threadFactory;
    private final int numberOfThread;
    private final String hostName;
    private final int port;

    private SimpleNettyServerFactory(String certPath, String keyPath, DependenciesResolver resolver, ThreadFactory threadFactory, int numberOfThread, String hostName, int port) {
        this.certPath = certPath;
        this.keyPath = keyPath;
        this.resolver = resolver;
        this.threadFactory = threadFactory;
        this.numberOfThread = numberOfThread;
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public NettyServer getServer() {
        SslContextProvider sslContextProvider = new SslContextProvider(certPath, keyPath);
        Http2ServerInitializer http2ServerInitializer = new Http2ServerInitializer(resolver, sslContextProvider);
        Http3ServerInitializer http3ServerInitializer = new Http3ServerInitializer(resolver);
        EpollEventLoopGroup eventExecutors = new EpollEventLoopGroup(numberOfThread, threadFactory);
        return new SimpleNettyServer(http2ServerInitializer, http3ServerInitializer, eventExecutors, hostName, port, sslContextProvider);
    }

    public static NettyServerFactory.Builder builder() {
        return new SimpleBuilder();
    }

    public static class SimpleBuilder implements NettyServerFactory.Builder {
        private String certPath;
        private String keyPath;
        private DependenciesResolver resolver;
        private ThreadFactory threadFactory;
        private int numberOfThread;
        private String hostName;
        private int port;

        @Override
        public Builder certPath(String certPath) {
            this.certPath = certPath;
            return this;
        }

        @Override
        public Builder keyPath(String keyPath) {
            this.keyPath = keyPath;
            return this;
        }

        @Override
        public Builder resolver(DependenciesResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        @Override
        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        @Override
        public Builder numberOfThread(int numberOfThread) {
            this.numberOfThread = numberOfThread;
            return this;
        }

        @Override
        public Builder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        @Override
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        @Override
        public NettyServerFactory build() {
            validate();
            return new SimpleNettyServerFactory(certPath, keyPath, resolver, threadFactory, numberOfThread, hostName, port);
        }

        private void validate() {
            if (!StringUtils.hasText(certPath)) {
                createException("Ssl cert path must be not null");
            }
            if (!StringUtils.hasText(keyPath)) {
                createException("Ssl key path must be not null");
            }
            if (resolver == null) {
                createException("Resolver must be not null");
            }
            if (threadFactory == null) {
                createException("ThreadFactory must be not null");
            }
            if (numberOfThread == 0) {
                numberOfThread = 15;
            }
            if (!StringUtils.hasText(hostName)) {
                createException("Host name must be not null");
            }
            if (port == 0) {
                createException("Port must be specify");
            }
        }

        private void createException(String message) {
            throw new IllegalArgumentException(message);
        }
    }
}
