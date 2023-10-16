package com.bachlinh.order.http.server.listener;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Stream;

public class Http2StreamListener implements Http2Connection.Listener {

    @Override
    public void onStreamAdded(Http2Stream stream) {
        // Do nothing
    }

    @Override
    public void onStreamActive(Http2Stream stream) {
        // Do nothing
    }

    @Override
    public void onStreamHalfClosed(Http2Stream stream) {
        // Do nothing
    }

    @Override
    public void onStreamClosed(Http2Stream stream) {
        // Do nothing
    }

    @Override
    public void onStreamRemoved(Http2Stream stream) {
        stream.close();
    }

    @Override
    public void onGoAwaySent(int lastStreamId, long errorCode, ByteBuf debugData) {
        // Do nothing
    }

    @Override
    public void onGoAwayReceived(int lastStreamId, long errorCode, ByteBuf debugData) {
        // Do nothing
    }
}
