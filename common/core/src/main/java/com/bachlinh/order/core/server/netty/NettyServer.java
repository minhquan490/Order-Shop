package com.bachlinh.order.core.server.netty;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.server.netty.channel.Http2ServerInitializer;
import com.bachlinh.order.core.server.netty.channel.Http3ServerInitializer;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.event.EventLoopGroupFactory;
import com.bachlinh.order.core.server.netty.ssl.SslContextProvider;
import com.bachlinh.order.service.container.DependenciesResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.incubator.codec.http3.Http3;
import io.netty.incubator.codec.quic.InsecureQuicTokenHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public final class NettyServer {
    private final String certPath;
    private final String keyPath;
    private final int port;
    private final String host;
    private final DependenciesResolver dependenciesResolver;

    private CompositeChannel compositeChannel;

    public NettyServer start() throws SSLException, InterruptedException {
        var executor = dependenciesResolver.resolveDependencies("threadPool");
        var eventLoopGroup = EventLoopGroupFactory.defaultInstance().createLoopGroup((Executor) executor);
        var sslProvider = new SslContextProvider(certPath, keyPath);

        var tcpChannel = configTcpChannel();
        var udpChannel = configUdpChannel(sslProvider, eventLoopGroup);
        this.compositeChannel = new CompositeChannel(tcpChannel, udpChannel);
        this.compositeChannel.start();
        return this;
    }

    public void stop() throws InterruptedException {
        this.compositeChannel.close();
    }

    private Channel configTcpChannel() throws SSLException, InterruptedException {
        log.info("Config tpc channel...");
        var executor = dependenciesResolver.resolveDependencies("threadPool");
        var servletHandlerAdapter = dependenciesResolver.resolveDependencies(ServletHandlerAdapter.class);
        var filterChainAdapter = dependenciesResolver.resolveDependencies(FilterChainAdapter.class);
        var eventLoopGroup = EventLoopGroupFactory.defaultInstance().createLoopGroup((Executor) executor);
        var sslProvider = new SslContextProvider(certPath, keyPath);
        var sslContext = sslProvider.createSslContext();
        var serverBootstrap = new ServerBootstrap();
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.group(eventLoopGroup);
        serverBootstrap.channel(EpollServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childHandler(new Http2ServerInitializer(sslContext, servletHandlerAdapter, filterChainAdapter));
        log.info("Config tcp channel done");
        return serverBootstrap.bind(host, port).sync().channel();
    }

    private Channel configUdpChannel(SslContextProvider sslProvider, EventLoopGroup eventLoopGroup) throws InterruptedException {
        log.info("Config udp channel...");
        var servletHandlerAdapter = dependenciesResolver.resolveDependencies(ServletHandlerAdapter.class);
        var filterChainAdapter = dependenciesResolver.resolveDependencies(FilterChainAdapter.class);
        var quicSslContext = sslProvider.createQuicSsl();
        var h3Handler = Http3.newQuicServerCodecBuilder()
                .sslContext(quicSslContext)
                .maxIdleTimeout(5000, TimeUnit.MILLISECONDS)
                .initialMaxData(10000000)
                .initialMaxStreamDataBidirectionalLocal(1000000)
                .initialMaxStreamDataBidirectionalRemote(1000000)
                .initialMaxStreamsBidirectional(100)
                .tokenHandler(InsecureQuicTokenHandler.INSTANCE)
                .handler(new Http3ServerInitializer(servletHandlerAdapter, filterChainAdapter))
                .build();
        var bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(EpollDatagramChannel.class);
        bootstrap.handler(h3Handler);
        log.info("Config udp channel done");
        return bootstrap.bind(host, port).sync().channel();
    }
}
