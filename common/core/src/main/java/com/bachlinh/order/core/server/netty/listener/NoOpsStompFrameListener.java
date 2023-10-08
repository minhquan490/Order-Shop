package com.bachlinh.order.core.server.netty.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.StompFrame;

public class NoOpsStompFrameListener implements StompFrameListener {
    @Override
    public void onConnect(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        ctx.close();
    }

    @Override
    public void onSubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        closeChannel(ctx);
    }

    @Override
    public void onSend(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        closeChannel(ctx);
    }

    @Override
    public void onUnsubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        closeChannel(ctx);
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        closeChannel(ctx);
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
