package com.bachlinh.order.http.server.shaded;

import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.util.internal.UnstableApi;

@UnstableApi
public final class Http2FrameStreamEvent {

    private final Http2FrameStream stream;
    private final io.netty.handler.codec.http2.Http2FrameStreamEvent.Type type;

    @UnstableApi
    public enum Type {
        State,
        Writability
    }

    private Http2FrameStreamEvent(Http2FrameStream stream, io.netty.handler.codec.http2.Http2FrameStreamEvent.Type type) {
        this.stream = stream;
        this.type = type;
    }

    public Http2FrameStream stream() {
        return stream;
    }

    public io.netty.handler.codec.http2.Http2FrameStreamEvent.Type type() {
        return type;
    }

    static Http2FrameStreamEvent stateChanged(Http2FrameStream stream) {
        return new Http2FrameStreamEvent(stream, io.netty.handler.codec.http2.Http2FrameStreamEvent.Type.State);
    }

    static Http2FrameStreamEvent writabilityChanged(Http2FrameStream stream) {
        return new Http2FrameStreamEvent(stream, io.netty.handler.codec.http2.Http2FrameStreamEvent.Type.Writability);
    }
}