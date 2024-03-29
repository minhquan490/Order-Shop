package com.bachlinh.order.http.server.shaded;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.internal.UnstableApi;

@UnstableApi
public class InboundHttpToHttp2Adapter extends ChannelInboundHandlerAdapter {
    private final Http2Connection connection;
    private final Http2FrameListener listener;

    public InboundHttpToHttp2Adapter(Http2Connection connection, Http2FrameListener listener) {
        this.connection = connection;
        this.listener = listener;
    }

    private static int getStreamId(Http2Connection connection, HttpHeaders httpHeaders) {
        return httpHeaders.getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(),
                connection.remote().incrementAndGetNextStreamId());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpMessage) {
            handle(ctx, connection, listener, (FullHttpMessage) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    // note that this may behave strangely when used for the initial upgrade
    // message when using h2c, since that message is ineligible for flow
    // control, but there is not yet an API for signaling that.
    static void handle(ChannelHandlerContext ctx, Http2Connection connection,
                       Http2FrameListener listener, FullHttpMessage message) throws Http2Exception {
        try {
            int streamId = getStreamId(connection, message.headers());
            Http2Stream stream = connection.stream(streamId);
            if (stream == null) {
                stream = connection.remote().createStream(streamId, false);
            }
            message.headers().set(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTP.name());
            Http2Headers messageHeaders = HttpConversionUtil.toHttp2Headers(message, true);
            boolean hasContent = message.content().isReadable();
            boolean hasTrailers = !message.trailingHeaders().isEmpty();
            listener.onHeadersRead(
                    ctx, streamId, messageHeaders, 0, !(hasContent || hasTrailers));
            if (hasContent) {
                listener.onDataRead(ctx, streamId, message.content(), 0, !hasTrailers);
            }
            if (hasTrailers) {
                Http2Headers headers = HttpConversionUtil.toHttp2Headers(message.trailingHeaders(), true);
                listener.onHeadersRead(ctx, streamId, headers, 0, true);
            }
            stream.closeRemoteSide();
        } finally {
            message.release();
        }
    }
}

