package com.bachlinh.order.core.server.netty.event;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import io.netty.handler.codec.http2.Http2Frame;

public interface Http2FrameListener extends FrameListener<Http2Frame> {
    static Http2FrameListener getDefaultInstance(ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        return new DefaultHttp2FrameListener(servletHandlerAdapter, filterChainAdapter);
    }
}
