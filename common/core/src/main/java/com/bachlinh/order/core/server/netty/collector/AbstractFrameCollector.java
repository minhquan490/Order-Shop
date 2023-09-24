package com.bachlinh.order.core.server.netty.collector;

import com.bachlinh.order.exception.system.server.PayloadToLargeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

public abstract class AbstractFrameCollector<T, U> implements FrameCollector<T, U> {
    private static final String URI_PATTERN = "{0}://{1}{2}";

    private final Queue<ByteBuf> dataPool = new ArrayDeque<>();

    private final String authority;
    private final String path;
    private final String scheme;
    private final String method;

    protected AbstractFrameCollector(String authority, String path, String scheme, String method) {
        this.authority = authority;
        this.path = path;
        this.scheme = scheme;
        this.method = method;
    }

    @Override
    public final void collectData(ByteBuf content) {
        this.dataPool.add(content);
    }

    @Override
    public final FullHttpRequest combine() {
        if (dataPool.isEmpty()) {
            return combineOnMethodGetOrHead();
        }
        ByteBuf buf;
        var maxCapacity = dataPool.stream().mapToLong(ByteBuf::readableBytes).sum();
        if (maxCapacity > Integer.MAX_VALUE) {
            throw new PayloadToLargeException("Too large");
        }
        ByteBuf data = Unpooled.buffer(0, (int) maxCapacity);
        while ((buf = dataPool.poll()) != null) {
            data.writeBytes(buf);
            buf.release();
        }
        var fullReq = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method.toUpperCase()), MessageFormat.format(URI_PATTERN, scheme, authority, path), data);
        if (isHeadersPoolEmpty()) {
            return fullReq;
        }
        combineHeader(fullReq);
        return fullReq;
    }

    @Override
    public final void release() {
        if (!dataPool.isEmpty()) {
            dataPool.clear();
        }
        if (!isHeadersPoolEmpty()) {
            releaseHeadersPool();
        }
    }

    @Override
    public final FullHttpRequest combineAndRelease() {
        try {
            return combine();
        } finally {
            release();
        }
    }

    @SuppressWarnings("unchecked")
    protected <K extends Headers<CharSequence, CharSequence, K>> void combineHeader(FullHttpRequest fullHttpRequest) {
        K combinedHeader;
        if (getHeadersPool().size() == 1) {
            combinedHeader = (K) getHeadersPool().peek();
        } else {
            Queue<K> transferred = new ArrayDeque<>(getHeadersPool());
            var optional = reduce(transferred);
            combinedHeader = optional.orElseGet(get());
        }
        if (combinedHeader != null) {
            fullHttpRequest.headers().add(toH1Header(combinedHeader));
        }
    }

    protected abstract boolean isHeadersPoolEmpty();

    protected abstract void releaseHeadersPool();

    protected abstract <K extends Headers<CharSequence, CharSequence, K>> Queue<K> getHeadersPool();

    protected abstract <K extends Headers<CharSequence, CharSequence, K>> Supplier<K> get();

    private <K extends Headers<CharSequence, CharSequence, K>> Optional<K> reduce(Queue<K> headersPool) {
        return headersPool.stream().reduce((entries, entries2) -> {
            for (java.util.Map.Entry<CharSequence, CharSequence> next : entries) {
                if (entries2.contains(next.getKey())) {
                    entries2.add(next.getKey(), next.getValue());
                } else {
                    entries2.set(next.getKey(), next.getValue());
                }
            }
            return entries2;
        });
    }

    private <K extends Headers<CharSequence, CharSequence, K>> HttpHeaders toH1Header(K headers) {
        var h1Header = new DefaultHttpHeaders(false);
        headers.forEach(h -> {
            var key = String.valueOf(h.getKey());
            if (!key.startsWith(":")) {
                h1Header.set(h.getKey(), h.getValue());
            }
        });
        return h1Header;
    }

    private FullHttpRequest combineOnMethodGetOrHead() {
        var fullReq = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method.toUpperCase()), MessageFormat.format(AbstractFrameCollector.URI_PATTERN, scheme, authority, path));
        combineHeader(fullReq);
        return fullReq;
    }
}
