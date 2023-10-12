package com.bachlinh.order.core.http.server.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.StompFrame;

public interface StompFrameListener {
    void onConnect(ChannelHandlerContext ctx, StompFrame inboundFrame);

    void onSubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame);

    void onSend(ChannelHandlerContext ctx, StompFrame inboundFrame);

    void onUnsubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame);

    void onDisconnect(ChannelHandlerContext ctx, StompFrame inboundFrame);
}
