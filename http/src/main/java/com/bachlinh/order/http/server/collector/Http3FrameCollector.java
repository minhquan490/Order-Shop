package com.bachlinh.order.http.server.collector;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.incubator.codec.http3.DefaultHttp3Headers;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Headers;
import io.netty.incubator.codec.http3.Http3HeadersFrame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class Http3FrameCollector extends AbstractFrameCollector<Http3HeadersFrame, Http3DataFrame> {

    private final Queue<Http3Headers> headerPool = new ArrayDeque<>();
    private Http2FrameStream streamFrame;

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

    public Collection<Http2HeadersFrame> toHttp2Header() {
        Collection<Http2Headers> http2Headers = new ArrayList<>(this.headerPool.size());
        for (Http3Headers http3Headers : this.headerPool) {
            Http2Headers headers = new DefaultHttp2Headers();
            headers.status(http3Headers.status());
            headers.scheme(http3Headers.scheme());
            headers.authority(http3Headers.authority());
            headers.path(http3Headers.path());
            headers.method(http3Headers.method());
            for (java.util.Map.Entry<CharSequence, CharSequence> next : http3Headers) {
                headers.add(next.getKey(), next.getValue());
            }
            http2Headers.add(headers);
        }
        return http2Headers.stream()
                .map(entries -> new DefaultHttp2HeadersFrame(entries, isDataAvailable()).stream(streamFrame))
                .map(Http2HeadersFrame.class::cast)
                .toList();
    }

    public Collection<Http2DataFrame> toHttp2Data() {
        Deque<Http2DataFrame> dataFrames = new LinkedList<>();

        for (ByteBuf data : dataPool()) {
            Http2DataFrame dataFrame = new DefaultHttp2DataFrame(data);
            dataFrames.add(dataFrame);
        }

        Http2DataFrame last = dataFrames.pollLast();
        if (last != null) {
            Http2DataFrame copied = new DefaultHttp2DataFrame(last.content(), true);
            dataFrames.addLast(copied);
        }

        return dataFrames;
    }

    public void setStreamFrame(Http2FrameStream streamFrame) {
        this.streamFrame = streamFrame;
    }

    public static Http3FrameCollector getInstance(String authority, String path, String scheme, String method) {
        return new Http3FrameCollector(authority, path, scheme, method);
    }
}
