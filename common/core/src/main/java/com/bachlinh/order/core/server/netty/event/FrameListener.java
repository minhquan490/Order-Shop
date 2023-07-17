package com.bachlinh.order.core.server.netty.event;

import io.netty.channel.ChannelHandlerContext;

public interface FrameListener<T> {
    <U extends T> void onHeaderFrame(U frame, ChannelHandlerContext context) throws Exception;

    <U extends T> void onDataFrame(U frame, ChannelHandlerContext context) throws Exception;

    <U extends T> void onGoAwayFrame(U frame, ChannelHandlerContext context) throws Exception;

    <U extends T> void onSettingsFrame(U frame, ChannelHandlerContext context) throws Exception;
}
