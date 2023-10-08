package com.bachlinh.order.core.server.netty.channel.http2;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.server.netty.channel.stomp.StompHandler;
import com.bachlinh.order.core.server.netty.listener.StompFrameListenerFactory;
import com.bachlinh.order.core.server.netty.shaded.IdleStateHandler;
import com.bachlinh.order.core.server.netty.ssl.SslContextProvider;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;

public class Http2ServerInitializer extends ChannelInitializer<EpollSocketChannel> {
    private final SslContext sslContext;
    private final ChannelHandler sharedHandler;
    private final StompFrameListenerFactory stompFrameListenerFactory;

    public Http2ServerInitializer(DependenciesResolver dependenciesResolver, Environment environment, SslContextProvider sslContextProvider) {
        try {
            this.sslContext = sslContextProvider.createSslContext();
        } catch (SSLException e) {
            throw new CriticalException(e);
        }
        this.sharedHandler = new Http2OrHttpHandler(dependenciesResolver, environment);
        this.stompFrameListenerFactory = (StompFrameListenerFactory) dependenciesResolver.resolveDependencies("stompFrameListener");
    }

    /**
     * Http2 connection initialize phase, call once when client establish connection to server.
     */
    @Override
    protected void initChannel(EpollSocketChannel ch) {
        ch.pipeline()
                .addLast(sslContext.newHandler(ch.alloc()))
                .addLast(new IdleStateHandler(0, 0, 60))
                .addLast(sharedHandler)
                .addLast(new StompHandler(stompFrameListenerFactory.createStompFrameListener()));
    }
}