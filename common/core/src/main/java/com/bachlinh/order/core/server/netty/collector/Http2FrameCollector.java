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
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Queue;

public class Http2FrameCollector implements FrameCollector<Http2HeadersFrame, Http2DataFrame> {
    private final Queue<ByteBuf> dataPool = new ArrayDeque<>();
    private final Queue<Http2Headers> headerPool = new ArrayDeque<>();

    private final String authority;
    private final String path;
    private final String scheme;
    private final String method;

    private Http2FrameCollector(String authority, String path, String scheme, String method) {
        this.authority = authority;
        this.path = path;
        this.scheme = scheme;
        this.method = method;
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
    public void collectData(ByteBuf content) {
        dataPool.add(content);
    }

    @Override
    public FullHttpRequest combine() {
        var uriPattern = "{0}://{1}{2}";
        if (dataPool.isEmpty()) {
            return combineOnMethodGetOrHead(uriPattern);
        }
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
        combineHeader(fullReq);
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

    private HttpHeaders toH1Header(Http2Headers http2Headers) {
        var h1Header = new DefaultHttpHeaders(false);
        http2Headers.forEach(h -> {
            var key = String.valueOf(h.getKey());
            if (!key.startsWith(":")) {
                h1Header.set(h.getKey(), h.getValue());
            }
        });
        return h1Header;
    }

    private FullHttpRequest combineOnMethodGetOrHead(String uriPattern) {
        var fullReq = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method.toUpperCase()), MessageFormat.format(uriPattern, scheme, authority, path));
        combineHeader(fullReq);
        return fullReq;
    }

    private void combineHeader(FullHttpRequest fullHttpRequest) {
        Http2Headers combinedHeader;
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
            combinedHeader = optional.orElseGet(DefaultHttp2Headers::new);
        }
        fullHttpRequest.headers().add(toH1Header(combinedHeader));
    }
}
