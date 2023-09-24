package com.bachlinh.order.core.server.netty;

import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.http2.CompressorHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2Settings;

public class Http2CompressFrameCodecBuilder extends Http2FrameCodecBuilder {

    @Override
    public Http2FrameCodec build() {
        Http2FrameCodec frameCodec = Http2FrameCodecBuilder.forServer()
                .initialSettings(new Http2Settings().pushEnabled(true).maxHeaderListSize(8192))
                .decoupleCloseAndGoAway(true)
                .build();

        server(true);
        gracefulShutdownTimeoutMillis(0);
        decoupleCloseAndGoAway(true);
        initialSettings(new Http2Settings().pushEnabled(true).maxHeaderListSize(8192));

        Http2ConnectionEncoder compressEncoder = new CompressorHttp2ConnectionEncoder(frameCodec.encoder(), StandardCompressionOptions.gzip());

        return super.build(frameCodec.decoder(), compressEncoder, initialSettings());
    }
}
