package com.bachlinh.order.core.server.netty.collector;

import com.bachlinh.order.exception.system.server.PayloadToLargeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.incubator.codec.http3.DefaultHttp3Headers;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Headers;
import io.netty.incubator.codec.http3.Http3HeadersFrame;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Queue;

public class Http3FrameCollector implements FrameCollector<Http3HeadersFrame, Http3DataFrame> {
    private final Queue<ByteBuf> dataPool = new ArrayDeque<>();
    private final Queue<Http3Headers> headerPool = new ArrayDeque<>();

    private final String authority;
    private final String path;
    private final String scheme;
    private final String method;

    private Http3FrameCollector(String authority, String path, String scheme, String method) {
        this.authority = authority;
        this.path = path;
        this.scheme = scheme;
        this.method = method;
    }

    public static Http3FrameCollector getInstance(CharSequence authority, CharSequence path, CharSequence scheme, CharSequence method) {
        return new Http3FrameCollector(authority.toString(), path.toString(), scheme.toString(), method.toString());
    }

    @Override
    public void collectHeader(Http3HeadersFrame headerFrame) {
        headerPool.add(headerFrame.headers());
    }

    @Override
    public void collectData(Http3DataFrame dataFrame) {
        collectData(dataFrame.content());
    }

    @Override
    public void collectData(ByteBuf content) {
        dataPool.add(content);
    }

    @Override
    public FullHttpRequest combine() {
        var uriPattern = "{0}://{1}/{2}";
        ByteBuf buf;
        var maxCapacity = dataPool.stream().mapToLong(ByteBuf::readableBytes).sum();
        if (maxCapacity > Integer.MAX_VALUE) {
            throw new PayloadToLargeException("Too large");
        }
        ByteBuf data = Unpooled.buffer(0, (int) maxCapacity);
        while ((buf = dataPool.peek()) != null) {
            data.writeBytes(buf);
            buf.release();
        }
        var fullReq = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method.toUpperCase()), MessageFormat.format(uriPattern, scheme, authority, path), data);
        if (headerPool.isEmpty()) {
            return fullReq;
        }
        Http3Headers combinedHeader;
        if (headerPool.size() == 1) {
            combinedHeader = headerPool.peek();
        } else {
            var optional = headerPool.stream().reduce((entries, entries2) -> {
                for (java.util.Map.Entry<CharSequence, CharSequence> next : entries) {
                    if (entries2.contains(next.getKey())) {
                        entries2.add(next.getKey(), next.getValue());
                    } else {
                        entries2.set(next.getKey(), next.getValue());
                    }
                }
                return entries2;
            });
            combinedHeader = optional.orElseGet(DefaultHttp3Headers::new);
        }
        fullReq.headers().add(toH1Header(combinedHeader));
        return fullReq;
    }

    @Override
    public void release() {
        if (!dataPool.isEmpty()) {
            dataPool.clear();
        }
        if (!headerPool.isEmpty()) {
            headerPool.clear();
        }
    }

    @Override
    public FullHttpRequest combineAndRelease() {
        try {
            return combine();
        } finally {
            release();
        }
    }

    private HttpHeaders toH1Header(Http3Headers http3Headers) {
        var h1Header = new DefaultHttpHeaders();
        http3Headers.forEach(h -> h1Header.set(h.getKey(), h.getValue()));
        return h1Header;
    }
}
