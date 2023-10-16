package com.bachlinh.order.http.server.channel.adapter;

import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.SocketAddress;

public class WrappedRequest extends DefaultFullHttpRequest {
    private FullHttpRequest actual;
    private SocketAddress remoteAddress;

    public WrappedRequest(FullHttpRequest actual) {
        this(actual.protocolVersion(), actual.method(), actual.uri());
        this.actual = actual;
    }

    public WrappedRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        super(httpVersion, method, uri);
    }

    public WrappedRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
        super(httpVersion, method, uri, content);
    }

    public WrappedRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
        super(httpVersion, method, uri, validateHeaders);
    }

    public WrappedRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, boolean validateHeaders) {
        super(httpVersion, method, uri, content, validateHeaders);
    }

    public WrappedRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeader) {
        super(httpVersion, method, uri, content, headers, trailingHeader);
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public FullHttpRequest getActual() {
        return actual;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public HttpHeaders headers() {
        return this.actual.headers();
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.actual.trailingHeaders();
    }

    @Override
    public ByteBuf content() {
        return this.actual.content();
    }

    @Override
    public boolean release() {
        return this.actual.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.actual.release(decrement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WrappedRequest that = (WrappedRequest) o;
        return Objects.equal(getActual(), that.getActual()) && Objects.equal(getRemoteAddress(), that.getRemoteAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getActual(), getRemoteAddress());
    }
}
