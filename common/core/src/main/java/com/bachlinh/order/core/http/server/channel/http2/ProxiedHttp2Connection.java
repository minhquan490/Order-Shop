package com.bachlinh.order.core.http.server.channel.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamVisitor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class ProxiedHttp2Connection implements Http2Connection {

    private final Http2Connection delegate;

    public ProxiedHttp2Connection(Http2Connection delegate) {
        this.delegate = delegate;
    }

    @Override
    public Future<Void> close(Promise<Void> promise) {
        return this.delegate.close(promise);
    }

    @Override
    public PropertyKey newKey() {
        return this.delegate.newKey();
    }

    @Override
    public void addListener(Listener listener) {
        this.delegate.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.delegate.removeListener(listener);
    }

    @Override
    public Http2Stream stream(int streamId) {
        return this.delegate.stream(streamId);
    }

    @Override
    public boolean streamMayHaveExisted(int streamId) {
        return this.delegate.streamMayHaveExisted(streamId);
    }

    @Override
    public Http2Stream connectionStream() {
        return this.delegate.connectionStream();
    }

    @Override
    public int numActiveStreams() {
        return this.delegate.numActiveStreams();
    }

    @Override
    public Http2Stream forEachActiveStream(Http2StreamVisitor visitor) throws Http2Exception {
        return this.delegate.forEachActiveStream(visitor);
    }

    @Override
    public boolean isServer() {
        return this.delegate.isServer();
    }

    @Override
    public Endpoint<Http2LocalFlowController> local() {
        return this.delegate.local();
    }

    @Override
    public Endpoint<Http2RemoteFlowController> remote() {
        return this.delegate.remote();
    }

    @Override
    public boolean goAwayReceived() {
        return this.delegate.goAwayReceived();
    }

    @Override
    public void goAwayReceived(int lastKnownStream, long errorCode, ByteBuf message) throws Http2Exception {
        this.delegate.goAwayReceived(lastKnownStream, errorCode, message);
    }

    @Override
    public boolean goAwaySent() {
        return this.delegate.goAwaySent();
    }

    @Override
    public boolean goAwaySent(int lastKnownStream, long errorCode, ByteBuf message) throws Http2Exception {
        return this.delegate.goAwaySent(lastKnownStream, errorCode, message);
    }
}
