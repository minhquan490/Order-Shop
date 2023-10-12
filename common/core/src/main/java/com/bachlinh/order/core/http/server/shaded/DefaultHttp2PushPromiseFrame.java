package com.bachlinh.order.core.http.server.shaded;

import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2PushPromiseFrame;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.util.internal.UnstableApi;

@UnstableApi
public final class DefaultHttp2PushPromiseFrame implements Http2PushPromiseFrame {

    private Http2FrameStream pushStreamFrame;
    private final Http2Headers http2Headers;
    private Http2FrameStream streamFrame;
    private final int padding;
    private final int promisedStreamId;

    public DefaultHttp2PushPromiseFrame(Http2Headers http2Headers) {
        this(http2Headers, 0);
    }

    public DefaultHttp2PushPromiseFrame(Http2Headers http2Headers, int padding) {
        this(http2Headers, padding, -1);
    }

    DefaultHttp2PushPromiseFrame(Http2Headers http2Headers, int padding, int promisedStreamId) {
        this.http2Headers = http2Headers;
        this.padding = padding;
        this.promisedStreamId = promisedStreamId;
    }

    @Override
    public Http2StreamFrame pushStream(Http2FrameStream stream) {
        pushStreamFrame = stream;
        return this;
    }

    @Override
    public Http2FrameStream pushStream() {
        return pushStreamFrame;
    }

    @Override
    public Http2Headers http2Headers() {
        return http2Headers;
    }

    @Override
    public int padding() {
        return padding;
    }

    @Override
    public int promisedStreamId() {
        if (pushStreamFrame != null) {
            return pushStreamFrame.id();
        } else {
            return promisedStreamId;
        }
    }

    @Override
    public Http2PushPromiseFrame stream(Http2FrameStream stream) {
        streamFrame = stream;
        return this;
    }

    @Override
    public Http2FrameStream stream() {
        return streamFrame;
    }

    @Override
    public String name() {
        return "PUSH_PROMISE_FRAME";
    }

    @Override
    public String toString() {
        return "DefaultHttp2PushPromiseFrame{" +
                "pushStreamFrame=" + pushStreamFrame +
                ", http2Headers=" + http2Headers +
                ", streamFrame=" + streamFrame +
                ", padding=" + padding +
                '}';
    }
}
