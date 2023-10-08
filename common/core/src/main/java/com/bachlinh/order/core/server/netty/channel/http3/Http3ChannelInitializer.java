package com.bachlinh.order.core.server.netty.channel.http3;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListenerFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.incubator.codec.http3.Http3Frame;
import io.netty.incubator.codec.quic.QuicStreamChannel;

@ChannelHandler.Sharable
public class Http3ChannelInitializer extends ChannelInitializer<QuicStreamChannel> {

    private final HttpFrameListenerFactory<Http3Frame> listenerFactory;

    @SuppressWarnings("unchecked")
    public Http3ChannelInitializer(DependenciesResolver resolver) {
        this.listenerFactory = (HttpFrameListenerFactory<Http3Frame>) resolver.resolveDependencies("http3FrameListener");
    }

    @Override
    protected void initChannel(QuicStreamChannel ch) {
        ch.pipeline().addLast(new Http3ChannelHandler(listenerFactory));
    }
}
