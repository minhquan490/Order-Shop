package com.bachlinh.order.core.server.netty.channel.http2;

import com.bachlinh.order.core.server.netty.ssl.SslContextProvider;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.container.DependenciesResolver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;

public class Http2ServerInitializer extends ChannelInitializer<EpollSocketChannel> {
    private final SslContext sslContext;
    private final ChannelHandler sharedHandler;

    public Http2ServerInitializer(DependenciesResolver dependenciesResolver, SslContextProvider sslContextProvider) {
        try {
            this.sslContext = sslContextProvider.createSslContext();
        } catch (SSLException e) {
            throw new CriticalException(e);
        }
        this.sharedHandler = new Http2OrHttpHandler(dependenciesResolver);
    }

    @Override
    protected void initChannel(EpollSocketChannel ch) {
        ch.pipeline().addLast(sslContext.newHandler(ch.alloc()), sharedHandler);
    }
}
