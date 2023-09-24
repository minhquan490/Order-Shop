package com.bachlinh.order.web.common.listener;

import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.collector.Http3FrameCollector;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListener;
import com.bachlinh.order.core.server.netty.utils.HandlerUtils;
import com.bachlinh.order.web.common.servlet.ServletRouter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Frame;
import io.netty.incubator.codec.http3.Http3Headers;
import io.netty.incubator.codec.http3.Http3HeadersFrame;

class DefaultHttp3Listener implements HttpFrameListener<Http3Frame> {
    private static final int FRAME_SIZE = 8000;

    private final ServletRouter servletRouter;
    private final FilterChainAdapter filterChainAdapter;

    private Http3FrameCollector frameCollector;

    DefaultHttp3Listener(ServletRouter servletRouter, FilterChainAdapter filterChainAdapter) {
        this.servletRouter = servletRouter;
        this.filterChainAdapter = filterChainAdapter;
    }

    @Override
    public <U extends Http3Frame> void onHeaderFrame(U frame, boolean isLast, ChannelHandlerContext ctx) {
        Http3HeadersFrame headersFrame = (Http3HeadersFrame) frame;
        Http3Headers headers = headersFrame.headers();
        frameCollector = Http3FrameCollector.getInstance(headers.authority().toString(), headers.path().toString(), headers.scheme().toString(), headers.method().toString());
        frameCollector.collectHeader(headersFrame);
        if (isLast) {
            writeResponse(ctx);
        }
    }

    @Override
    public <U extends Http3Frame> void onDataFrame(U frame, boolean isLast, ChannelHandlerContext ctx) {
        Http3DataFrame dataFrame = (Http3DataFrame) frame;
        frameCollector.collectData(dataFrame);
        if (isLast) {
            writeResponse(ctx);
        }
    }

    @Override
    public <U extends Http3Frame> void onGoAwayFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onPingFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onPriorityFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onPushPromiseFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onResetFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onSettingsAckFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onSettingsFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onUnknownFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http3Frame> void onWindowUpdateFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        try {
            NettyServletResponseAdapter adapter = HandlerUtils.handle(frameCollector, filterChainAdapter, ctx, (servletRouter::handleRequest));
            Http3HeadersFrame http3HeadersFrame = adapter.toH3HeaderFrame();
            Http3DataFrame[] http3DataFrames = adapter.toH3DataFrames(FRAME_SIZE);
            ctx.write(http3HeadersFrame);
            for (Http3DataFrame frame : http3DataFrames) {
                ctx.write(frame);
            }
        } catch (Exception e) {
            closeConnection(ctx);
        }
    }

    private void closeConnection(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
