package com.bachlinh.order.core.http.server.collector;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

public interface FrameCollector<T, U> {
    void collectHeader(T headerFrame);

    void collectData(U dataFrame);

    void collectData(ByteBuf content);

    FullHttpRequest combine();

    void release();

    FullHttpRequest combineAndRelease();
}
