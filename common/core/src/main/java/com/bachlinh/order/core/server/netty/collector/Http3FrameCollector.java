package com.bachlinh.order.core.server.netty.collector;

import io.netty.handler.codec.Headers;
import io.netty.incubator.codec.http3.DefaultHttp3Headers;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Headers;
import io.netty.incubator.codec.http3.Http3HeadersFrame;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class Http3FrameCollector extends AbstractFrameCollector<Http3HeadersFrame, Http3DataFrame> {

    private final Queue<Http3Headers> headerPool = new ArrayDeque<>();

    private Http3FrameCollector(String authority, String path, String scheme, String method) {
        super(authority, path, scheme, method);
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
        return () -> (K) new DefaultHttp3Headers();
    }

    @Override
    public void collectHeader(Http3HeadersFrame headerFrame) {
        headerPool.add(headerFrame.headers());
    }

    @Override
    public void collectData(Http3DataFrame dataFrame) {
        collectData(dataFrame.content());
    }

    public static Http3FrameCollector getInstance(String authority, String path, String scheme, String method) {
        return new Http3FrameCollector(authority, path, scheme, method);
    }
}
