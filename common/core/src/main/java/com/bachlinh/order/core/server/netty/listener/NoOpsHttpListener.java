package com.bachlinh.order.core.server.netty.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class NoOpsHttpListener implements HttpFrameListener<FullHttpRequest> {
    @Override
    public <U extends FullHttpRequest> void onHeaderFrame(U frame, boolean isLast, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onDataFrame(U frame, boolean isLast, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onGoAwayFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onPingFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onPriorityFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onPushPromiseFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onResetFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onSettingsAckFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onSettingsFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onUnknownFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public <U extends FullHttpRequest> void onWindowUpdateFrame(U frame, ChannelHandlerContext ctx) {
        close(ctx);
    }

    private void close(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
