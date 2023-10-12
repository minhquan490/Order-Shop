package com.bachlinh.order.core.http.server.channel.http3;

import io.netty.channel.ChannelInitializer;
import io.netty.incubator.codec.http3.Http3ServerConnectionHandler;
import io.netty.incubator.codec.quic.QuicChannel;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.http.server.shaded.IdleStateHandler;

public class Http3ServerInitializer extends ChannelInitializer<QuicChannel> {

    private final DependenciesResolver resolver;

    public Http3ServerInitializer(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void initChannel(QuicChannel ch) {
        ch.pipeline().addLast(new IdleStateHandler(0, 0, 60));
        ch.pipeline().addLast(new Http3ServerConnectionHandler(new Http3ChannelInitializer(resolver)));
    }
}