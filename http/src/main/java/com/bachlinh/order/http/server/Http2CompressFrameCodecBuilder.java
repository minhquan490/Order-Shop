package com.bachlinh.order.http.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.http2.CompressorHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.DefaultHttp2HeadersDecoder;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2PromisedRequestVerifier;
import io.netty.handler.codec.http2.Http2Settings;

import com.bachlinh.order.http.server.channel.http2.ProxiedHttp2Connection;
import com.bachlinh.order.http.server.listener.Http2StreamListener;
import com.bachlinh.order.http.server.shaded.Http2FrameCodec;
import com.bachlinh.order.http.server.shaded.Http2FrameCodecBuilder;

public class Http2CompressFrameCodecBuilder extends Http2FrameCodecBuilder {

    @Override
    public Http2FrameCodec build() {

        Http2Connection connection = buildConnection();
        Http2ConnectionEncoder encoder = buildEncoder(connection);
        Http2ConnectionDecoder decoder = buildDecoder(connection, encoder);

        Http2FrameCodec handler = build(decoder, encoder, settings());

        handler.gracefulShutdownTimeoutMillis(0);

        return handler;
    }

    private Http2Connection buildConnection() {
        Http2Connection delegate = new DefaultHttp2Connection(true, 1000);
        var proxiedConnection = new ProxiedHttp2Connection(delegate);
        Http2StreamListener listener = new Http2StreamListener();
        proxiedConnection.addListener(listener);
        return proxiedConnection;
    }

    private Http2FrameReader buildFrameReader() {
        long maxHeaderListSize = 8192;
        DefaultHttp2HeadersDecoder decoder = new DefaultHttp2HeadersDecoder(true, maxHeaderListSize, -1);
        return new DefaultHttp2FrameReader(decoder);
    }

    private Http2FrameWriter buildFrameWriter() {
        return new DefaultHttp2FrameWriter();
    }

    private Http2ConnectionEncoder buildEncoder(Http2Connection connection) {
        Http2FrameWriter frameWriter = buildFrameWriter();
        Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, frameWriter);
        return new CompressorHttp2ConnectionEncoder(encoder, StandardCompressionOptions.gzip());
    }

    private Http2ConnectionDecoder buildDecoder(Http2Connection connection, Http2ConnectionEncoder encoder) {
        Http2FrameReader frameReader = buildFrameReader();
        return new DefaultHttp2ConnectionDecoder(connection, encoder, frameReader, always(), true, true);
    }

    private Http2PromisedRequestVerifier always() {
        return new Http2PromisedRequestVerifier() {
            @Override
            public boolean isAuthoritative(ChannelHandlerContext ctx, Http2Headers headers) {
                return true;
            }

            @Override
            public boolean isCacheable(Http2Headers headers) {
                return true;
            }

            @Override
            public boolean isSafe(Http2Headers headers) {
                return true;
            }
        };
    }

    private Http2Settings settings() {
        Http2Settings settings = new Http2Settings();
        settings.maxConcurrentStreams(10000);
        settings.maxHeaderListSize(8192);
        return settings;
    }
}
