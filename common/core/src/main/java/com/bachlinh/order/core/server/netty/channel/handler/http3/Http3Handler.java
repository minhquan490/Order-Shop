package com.bachlinh.order.core.server.netty.channel.handler.http3;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.collector.Http3FrameCollector;
import com.bachlinh.order.core.server.netty.strategy.NettyHandlerContextStrategy;
import com.bachlinh.order.core.server.netty.utils.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import io.netty.incubator.codec.http3.Http3RequestStreamInboundHandler;
import jakarta.servlet.http.HttpServletResponse;

public class Http3Handler extends Http3RequestStreamInboundHandler {
    private final ServletHandlerAdapter servletHandlerAdapter;
    private final NettyHandlerContextStrategy strategy = NettyHandlerContextStrategy.getHttp3Strategy();
    private final HttpServletResponse sharedResponse = NettyServletResponseAdapter.getInstance();
    private final FilterChainAdapter filterChainAdapter;

    private Http3FrameCollector frameCollector;

    public Http3Handler(ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        this.servletHandlerAdapter = servletHandlerAdapter;
        this.filterChainAdapter = filterChainAdapter;
    }

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame, boolean isLast) {
        if (frameCollector == null) {
            var headers = frame.headers();
            frameCollector = Http3FrameCollector.getInstance(headers.authority(), headers.path(), headers.scheme(), headers.method());
        }
        frameCollector.collectHeader(frame);
    }

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame, boolean isLast) throws Exception {
        if (isLast) {
            frameCollector.collectData(frame);
            var fullReq = frameCollector.combineAndRelease();
            filterChainAdapter.interceptRequest(fullReq, ctx, sharedResponse, strategy);
            HttpUtils.writeResp(ctx, strategy, fullReq, servletHandlerAdapter, sharedResponse);
        } else {
            frameCollector.collectData(frame);
        }
    }
}
