package com.bachlinh.order.http.server.channel.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Frame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import io.netty.incubator.codec.http3.Http3RequestStreamInboundHandler;

import com.bachlinh.order.http.server.listener.HttpFrameListener;
import com.bachlinh.order.http.server.listener.HttpFrameListenerFactory;

public class Http3ChannelHandler extends Http3RequestStreamInboundHandler {

    private final HttpFrameListener<Http3Frame> http3FrameHttpFrameListener;

    public Http3ChannelHandler(HttpFrameListenerFactory<Http3Frame> listenerFactory) {
        this.http3FrameHttpFrameListener = listenerFactory.createFrameListener();
    }

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame, boolean isLast) {
        http3FrameHttpFrameListener.onHeaderFrame(frame, isLast, ctx);
    }

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame, boolean isLast) {
        http3FrameHttpFrameListener.onDataFrame(frame, isLast, ctx);
    }
}
