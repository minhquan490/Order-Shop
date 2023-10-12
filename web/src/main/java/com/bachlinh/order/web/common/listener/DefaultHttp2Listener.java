package com.bachlinh.order.web.common.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2PushPromiseFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.core.function.Decorator;
import com.bachlinh.order.core.http.handler.Router;
import com.bachlinh.order.core.http.server.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.core.http.server.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.http.server.collector.Http2FrameCollector;
import com.bachlinh.order.core.http.server.listener.HttpFrameListener;
import com.bachlinh.order.core.utils.HandlerUtils;

import java.io.IOException;

class DefaultHttp2Listener implements HttpFrameListener<Http2Frame> {
    private static final int FRAME_SIZE = 8000;
    private static final Decorator<Http2HeadersFrame> HTTP_2_HEADERS_FRAME_DECORATOR;

    static {
        HTTP_2_HEADERS_FRAME_DECORATOR = target -> target.headers().set(HttpHeaderNames.CONTENT_ENCODING.toLowerCase(), "gzip");
    }

    private final Router<Object, Object> servletRouter;
    private final FilterChainAdapter filterChainAdapter;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Http2FrameCollector frameCollector;

    DefaultHttp2Listener(Router<Object, Object> servletRouter, FilterChainAdapter filterChainAdapter) {
        this.servletRouter = servletRouter;
        this.filterChainAdapter = filterChainAdapter;
    }

    @Override
    public <U extends Http2Frame> void onHeaderFrame(U frame, boolean isLast, ChannelHandlerContext ctx) {
        Http2HeadersFrame headersFrame = (Http2HeadersFrame) frame;
        Http2FrameStream streamFrame = headersFrame.stream();
        Http2Headers headers = headersFrame.headers();
        frameCollector = Http2FrameCollector.getInstance(headers.authority(), headers.path(), headers.scheme(), headers.method());
        frameCollector.collectHeader(headersFrame);
        if (isLast) {
            writeResponse(streamFrame, ctx);
        }
    }

    @Override
    public <U extends Http2Frame> void onDataFrame(U frame, boolean isLast, ChannelHandlerContext ctx) {
        Http2DataFrame dataFrame = (Http2DataFrame) frame;
        Http2FrameStream streamFrame = dataFrame.stream();
        frameCollector.collectData(dataFrame);
        if (isLast) {
            writeResponse(streamFrame, ctx);
        }
    }

    @Override
    public <U extends Http2Frame> void onGoAwayFrame(U frame, ChannelHandlerContext ctx) {
        closeConnection(ctx);
    }

    @Override
    public <U extends Http2Frame> void onPingFrame(U frame, ChannelHandlerContext ctx) {
        //Do nothing
    }

    @Override
    public <U extends Http2Frame> void onPriorityFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http2Frame> void onPushPromiseFrame(U frame, ChannelHandlerContext ctx) {
        Http2PushPromiseFrame pushPromiseFrame = (Http2PushPromiseFrame) frame;
        Http2FrameStream stream = pushPromiseFrame.pushStream();
        Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(pushPromiseFrame.http2Headers());
        frameCollector.collectHeader(headersFrame);
        writeResponse(stream, ctx);
    }

    @Override
    public <U extends Http2Frame> void onResetFrame(U frame, ChannelHandlerContext ctx) {
        closeConnection(ctx);
    }

    @Override
    public <U extends Http2Frame> void onSettingsAckFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http2Frame> void onSettingsFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    @Override
    public <U extends Http2Frame> void onUnknownFrame(U frame, ChannelHandlerContext ctx) {
        closeConnection(ctx);
    }

    @Override
    public <U extends Http2Frame> void onWindowUpdateFrame(U frame, ChannelHandlerContext ctx) {
        // Do nothing
    }

    private void writeResponse(Http2FrameStream streamFrame, ChannelHandlerContext ctx) {
        try {
            NettyServletResponseAdapter adapter = HandlerUtils.handle(frameCollector, filterChainAdapter, ctx, (servletRouter::handleRequest));
            writeHeader(streamFrame, ctx, adapter);
            writeBody(streamFrame, ctx, adapter);
        } catch (Exception e) {
            logger.error("Error when handle request !", e);
            closeConnection(ctx);
        }
    }

    private void writeHeader(Http2FrameStream streamFrame, ChannelHandlerContext ctx, NettyServletResponseAdapter adapter) throws IOException {
        Http2HeadersFrame http2HeadersFrame = adapter.toH2HeaderFrame(streamFrame);
        HTTP_2_HEADERS_FRAME_DECORATOR.decorate(http2HeadersFrame);
        if (http2HeadersFrame.isEndStream()) {
            ctx.writeAndFlush(http2HeadersFrame);
        } else {
            ctx.write(http2HeadersFrame);
        }
    }

    private void writeBody(Http2FrameStream streamFrame, ChannelHandlerContext ctx, NettyServletResponseAdapter adapter) {
        Http2DataFrame[] dataFrames = adapter.toH2DataFrame(streamFrame, FRAME_SIZE);
        for (Http2DataFrame dataFrame : dataFrames) {
            if (dataFrame.isEndStream()) {
                ctx.writeAndFlush(dataFrame);
            }
            ctx.write(dataFrame);
        }
    }

    private void closeConnection(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
