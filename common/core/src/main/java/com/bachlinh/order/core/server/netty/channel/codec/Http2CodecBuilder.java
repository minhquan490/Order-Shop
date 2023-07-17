package com.bachlinh.order.core.server.netty.channel.codec;

import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.http2.CompressorHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Settings;

public class Http2CodecBuilder {

    public Http2CodecHolder buildHttp2Codec() {
        var connection = new DefaultHttp2Connection(true);
        var defaultEncoder = new DefaultHttp2ConnectionEncoder(connection, new DefaultHttp2FrameWriter());
        var encoder = new CompressorHttp2ConnectionEncoder(defaultEncoder, StandardCompressionOptions.gzip());
        var wrappedEncoder = new Http2ControlFrameLimitEncoder(encoder);
        var decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, new DefaultHttp2FrameReader());
        return new Http2CodecHolder(wrappedEncoder, decoder, new Http2Settings());
    }

    public static class Http2CodecHolder {
        private final Http2ConnectionEncoder encoder;
        private final Http2ConnectionDecoder decoder;
        private final Http2Settings settings;

        private Http2CodecHolder(Http2ConnectionEncoder encoder, Http2ConnectionDecoder decoder, Http2Settings settings) {
            this.encoder = encoder;
            this.decoder = decoder;
            this.settings = settings;
        }

        public Http2ConnectionDecoder getDecoder() {
            return decoder;
        }

        public Http2ConnectionEncoder getEncoder() {
            return encoder;
        }

        public Http2Settings getSettings() {
            return settings;
        }
    }
}
