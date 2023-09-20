package com.bachlinh.order.core.server.netty.channel.handler.http2;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.event.Http2FrameListener;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.util.CharsetUtil;

public class Http2Handler extends ChannelInboundHandlerAdapter {
    private final Http2FrameListener frameListener;

    public Http2Handler(ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        this.frameListener = Http2FrameListener.getDefaultInstance(servletHandlerAdapter, filterChainAdapter);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame msgHeader) {
            frameListener.onHeaderFrame(msgHeader, ctx);
            return;
        }
        if (msg instanceof Http2DataFrame dataFrame) {
            frameListener.onDataFrame(dataFrame, ctx);
            return;
        }
        if (msg instanceof Http2SettingsFrame settingsFrame) {
            frameListener.onSettingsFrame(settingsFrame, ctx);
            return;
        }
        if (msg instanceof Http2GoAwayFrame goAwayFrame) {
            frameListener.onGoAwayFrame(goAwayFrame, ctx);
            return;
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
        ctx.write(new DefaultHttp2HeadersFrame(headers));
        ctx.write(new DefaultHttp2DataFrame(Unpooled.copiedBuffer(cause.getClass().getName(), CharsetUtil.UTF_8), true));
        ctx.close();
    }
}
