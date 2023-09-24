package com.bachlinh.order.core.server.netty.utils;

import com.bachlinh.order.core.function.ServletCallback;
import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.collector.FrameCollector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class HandlerUtils {
    private HandlerUtils() {
    }

    public static NettyServletResponseAdapter handle(FrameCollector<?, ?> frameCollector, FilterChainAdapter filterChainAdapter, ChannelHandlerContext ctx, ServletCallback callback) throws ServletException, IOException {
        FullHttpRequest fullHttpRequest = frameCollector.combineAndRelease();
        HttpServletResponse response = NettyServletResponseAdapter.getInstance();
        filterChainAdapter.interceptRequest(fullHttpRequest, ctx, response, callback);
        return (NettyServletResponseAdapter) response;
    }
}
