package com.bachlinh.order.http.server.channel.http2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2PingFrame;
import io.netty.handler.codec.http2.Http2PriorityFrame;
import io.netty.handler.codec.http2.Http2PushPromiseFrame;
import io.netty.handler.codec.http2.Http2ResetFrame;
import io.netty.handler.codec.http2.Http2SettingsAckFrame;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.handler.codec.http2.Http2UnknownFrame;
import io.netty.handler.codec.http2.Http2WindowUpdateFrame;

import com.bachlinh.order.http.server.listener.HttpFrameListener;
import com.bachlinh.order.http.server.listener.HttpFrameListenerFactory;

import org.springframework.lang.NonNull;

public class DefaultHttp2Handler extends ChannelDuplexHandler {

    private final HttpFrameListener<Http2Frame> http2FrameHttpFrameListener;

    public DefaultHttp2Handler(HttpFrameListenerFactory<Http2Frame> listenerFactory) {
        this.http2FrameHttpFrameListener = listenerFactory.createFrameListener();
    }

    @Override
    public void channelRead(@NonNull ChannelHandlerContext ctx, Object msg) {
        String messageName = msg.getClass().getSimpleName();
        switch (messageName) {
            case "DefaultHttp2DataFrame" ->
                    http2FrameHttpFrameListener.onDataFrame((Http2DataFrame) msg, ((Http2DataFrame) msg).isEndStream(), ctx);
            case "DefaultHttp2GoAwayFrame" -> http2FrameHttpFrameListener.onGoAwayFrame((Http2GoAwayFrame) msg, ctx);
            case "DefaultHttp2HeadersFrame" ->
                    http2FrameHttpFrameListener.onHeaderFrame((Http2HeadersFrame) msg, ((Http2HeadersFrame) msg).isEndStream(), ctx);
            case "DefaultHttp2PingFrame" -> http2FrameHttpFrameListener.onPingFrame((Http2PingFrame) msg, ctx);
            case "DefaultHttp2PriorityFrame" ->
                    http2FrameHttpFrameListener.onPriorityFrame((Http2PriorityFrame) msg, ctx);
            case "DefaultHttp2PushPromiseFrame" ->
                    http2FrameHttpFrameListener.onPushPromiseFrame((Http2PushPromiseFrame) msg, ctx);
            case "DefaultHttp2ResetFrame" -> http2FrameHttpFrameListener.onResetFrame((Http2ResetFrame) msg, ctx);
            case "DefaultHttp2SettingsAckFrame" ->
                    http2FrameHttpFrameListener.onSettingsAckFrame((Http2SettingsAckFrame) msg, ctx);
            case "DefaultHttp2SettingsFrame" ->
                    http2FrameHttpFrameListener.onSettingsFrame((Http2SettingsFrame) msg, ctx);
            case "DefaultHttp2UnknownFrame" -> http2FrameHttpFrameListener.onUnknownFrame((Http2UnknownFrame) msg, ctx);
            case "DefaultHttp2WindowUpdateFrame" ->
                    http2FrameHttpFrameListener.onWindowUpdateFrame((Http2WindowUpdateFrame) msg, ctx);
            default -> {/* Do nothing */}
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
