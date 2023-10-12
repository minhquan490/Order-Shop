package com.bachlinh.order.core.http.server.listener;

import io.netty.channel.ChannelHandlerContext;

public interface HttpFrameListener<T> {
    <U extends T> void onHeaderFrame(U frame, boolean isLast, ChannelHandlerContext ctx);

    <U extends T> void onDataFrame(U frame, boolean isLast, ChannelHandlerContext ctx);

    <U extends T> void onGoAwayFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onPingFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onPriorityFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onPushPromiseFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onResetFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onSettingsAckFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onSettingsFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onUnknownFrame(U frame, ChannelHandlerContext ctx);

    <U extends T> void onWindowUpdateFrame(U frame, ChannelHandlerContext ctx);
}
