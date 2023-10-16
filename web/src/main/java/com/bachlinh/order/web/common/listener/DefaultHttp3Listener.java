package com.bachlinh.order.web.common.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Frame;
import io.netty.incubator.codec.http3.Http3Headers;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import io.netty.incubator.codec.quic.QuicStreamChannel;

import com.bachlinh.order.http.handler.Router;
import com.bachlinh.order.http.server.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.http.server.channel.security.FilterChainAdapter;
import com.bachlinh.order.http.server.collector.Http3FrameCollector;
import com.bachlinh.order.http.server.listener.HttpFrameListener;
import com.bachlinh.order.http.utils.HandlerUtils;

class DefaultHttp3Listener implements HttpFrameListener<Http3Frame> {
    private static final int FRAME_SIZE = 8000;

    private final Router<Object, Object> servletRouter;
    private final FilterChainAdapter filterChainAdapter;

    private Http3FrameCollector frameCollector;

    DefaultHttp3Listener(Router<Object, Object> servletRouter, FilterChainAdapter filterChainAdapter) {
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
            NettyServletResponseAdapter adapter = HandlerUtils.handle(frameCollector, filterChainAdapter, ctx, params -> servletRouter.handleRequest(params[0], params[1]));
            Http3HeadersFrame http3HeadersFrame = adapter.toH3HeaderFrame();
            Http3DataFrame[] http3DataFrames = adapter.toH3DataFrames(FRAME_SIZE);
            if (http3DataFrames == null || http3DataFrames.length == 0) {
                ctx.writeAndFlush(http3DataFrames).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
            } else {
                ctx.write(http3HeadersFrame);
                for (int i = 0; i < http3DataFrames.length; i++) {
                    Http3DataFrame frame = http3DataFrames[i];
                    if (i == http3DataFrames.length - 1) {
                        ctx.writeAndFlush(frame).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
                    } else {
                        ctx.write(frame);
                    }
                }
                ctx.flush();
            }
        } catch (Exception e) {
            closeConnection(ctx);
        }
    }

    private void closeConnection(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
