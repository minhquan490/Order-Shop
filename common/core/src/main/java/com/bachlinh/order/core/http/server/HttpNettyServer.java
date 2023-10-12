package com.bachlinh.order.core.http.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import com.bachlinh.order.core.http.server.channel.http2.Http2ServerInitializer;
import com.bachlinh.order.core.http.server.channel.http3.Http3ServerInitializer;
import com.bachlinh.order.core.http.server.ssl.SslContextProvider;
import com.bachlinh.order.core.utils.NettyServerUtils;

import java.net.InetSocketAddress;

class HttpNettyServer implements NettyServer {

    private final Http2ServerInitializer http2ServerInitializer;
    private final Http3ServerInitializer http3ServerInitializer;
    private final EpollEventLoopGroup workerExecutor;
    private final String hostName;
    private final int port;
    private final SslContextProvider sslContextProvider;

    HttpNettyServer(Http2ServerInitializer http2ServerInitializer, Http3ServerInitializer http3ServerInitializer, EpollEventLoopGroup workerExecutor, String hostName, int port, SslContextProvider sslContextProvider) {
        this.http2ServerInitializer = http2ServerInitializer;
        this.http3ServerInitializer = http3ServerInitializer;
        this.workerExecutor = workerExecutor;
        this.hostName = hostName;
        this.port = port;
        this.sslContextProvider = sslContextProvider;
    }

    @Override
    public void start() throws InterruptedException {
        startHttp2Server();
        startHttp3Server();
    }

    @Override
    public void shutdown() {
        shutdownHttp2Server();
    }

    private void startHttp2Server() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.option(ChannelOption.AUTO_CLOSE, true);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.group(workerExecutor)
                .channel(EpollServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(http2ServerInitializer);
        serverBootstrap.bind(hostName, port).sync();
    }

    private void startHttp3Server() throws InterruptedException {
        NettyServerUtils.NettyHttp3ServerOption option = NettyServerUtils.createHttp3Option(sslContextProvider, http3ServerInitializer);
        Bootstrap bootstrap = option.getHttp3Bootstrap();
        ChannelHandler codec = option.getHttp3Codec();
        Channel channel = bootstrap.group(workerExecutor)
                .channel(EpollDatagramChannel.class)
                .handler(codec)
                .bind(new InetSocketAddress(hostName, port))
                .channel();
        channel.closeFuture().sync();
    }

    private void shutdownHttp2Server() {
        workerExecutor.shutdownGracefully();
    }
}
