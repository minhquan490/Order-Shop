package com.bachlinh.order.core.server.netty.channel.http3;

import com.bachlinh.order.service.container.DependenciesResolver;
import io.netty.channel.ChannelInitializer;
import io.netty.incubator.codec.http3.Http3ServerConnectionHandler;
import io.netty.incubator.codec.quic.QuicChannel;

public class Http3ServerInitializer extends ChannelInitializer<QuicChannel> {

    private final DependenciesResolver resolver;

    public Http3ServerInitializer(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void initChannel(QuicChannel ch) {
        ch.pipeline().addLast(new Http3ServerConnectionHandler(new Http3ChannelInitializer(resolver)));
    }
}
