package com.bachlinh.order.core.http.server.collector;

import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class Http2FrameCollector extends AbstractFrameCollector<Http2HeadersFrame, Http2DataFrame> {
    private final Queue<Http2Headers> headerPool = new ArrayDeque<>();

    private Http2FrameCollector(String authority, String path, String scheme, String method) {
        super(authority, path, scheme, method);
    }

    public static Http2FrameCollector getInstance(CharSequence authority, CharSequence path, CharSequence scheme, CharSequence method) {
        return new Http2FrameCollector(authority.toString(), path.toString(), scheme.toString(), method.toString());
    }

    @Override
    public void collectHeader(Http2HeadersFrame headerFrame) {
        headerPool.add(headerFrame.headers());
    }

    @Override
    public void collectData(Http2DataFrame dataFrame) {
        collectData(dataFrame.content());
    }

    @Override
    protected boolean isHeadersPoolEmpty() {
        return headerPool.isEmpty();
    }

    @Override
    protected void releaseHeadersPool() {
        headerPool.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends Headers<CharSequence, CharSequence, K>> Queue<K> getHeadersPool() {
        return (Queue<K>) headerPool;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K extends Headers<CharSequence, CharSequence, K>> Supplier<K> get() {
        return () -> (K) new DefaultHttp2Headers();
    }
}
