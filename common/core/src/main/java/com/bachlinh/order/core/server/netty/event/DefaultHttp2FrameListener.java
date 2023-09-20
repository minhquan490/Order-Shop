package com.bachlinh.order.core.server.netty.event;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletResponseAdapter;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.collector.Http2FrameCollector;
import com.bachlinh.order.core.server.netty.strategy.Http2Strategy;
import com.bachlinh.order.core.server.netty.strategy.NettyHandlerContextStrategy;
import com.bachlinh.order.core.server.netty.utils.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import jakarta.servlet.http.HttpServletResponse;

public class DefaultHttp2FrameListener implements Http2FrameListener {
    private final ServletHandlerAdapter servletHandlerAdapter;
    private final FilterChainAdapter filterChainAdapter;
    private final HttpServletResponse sharedResponse = NettyServletResponseAdapter.getInstance();
    private final NettyHandlerContextStrategy strategy = NettyHandlerContextStrategy.getHttp2Strategy();
    private Http2FrameCollector frameCollector;

    protected DefaultHttp2FrameListener(ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        this.servletHandlerAdapter = servletHandlerAdapter;
        this.filterChainAdapter = filterChainAdapter;
    }

    @Override
    public <U extends Http2Frame> void onHeaderFrame(U frame, ChannelHandlerContext context) {
        Http2HeadersFrame headersFrame = (Http2HeadersFrame) frame;
        if (frameCollector == null) {
            var headers = headersFrame.headers();
            frameCollector = Http2FrameCollector.getInstance(headers.authority(), headers.path(), headers.scheme(), headers.method());
        }
        frameCollector.collectHeader(headersFrame);
        if (headersFrame.isEndStream()) {
            var req = frameCollector.combineAndRelease();
            try {
                filterChainAdapter.interceptRequest(req, context, sharedResponse, strategy);
                ((Http2Strategy) strategy).setHeadersFrame(headersFrame);
                HttpUtils.writeResp(context, strategy, req, servletHandlerAdapter, sharedResponse);
            } catch (Exception e) {
                context.fireExceptionCaught(e);
            }
        }
    }

    @Override
    public <U extends Http2Frame> void onDataFrame(U frame, ChannelHandlerContext context) {
        Http2DataFrame dataFrame = (Http2DataFrame) frame;
        if (dataFrame.isEndStream()) {
            frameCollector.collectData(dataFrame);
            try {
                var fullReq = frameCollector.combineAndRelease();
                filterChainAdapter.interceptRequest(fullReq, context, sharedResponse, strategy);
                ((Http2Strategy) strategy).setDataFrame(dataFrame);
                HttpUtils.writeResp(context, strategy, fullReq, servletHandlerAdapter, sharedResponse);
            } catch (Exception e) {
                context.fireExceptionCaught(e);
            }
        } else {
            frameCollector.collectData(dataFrame);
        }
    }

    @Override
    public <U extends Http2Frame> void onGoAwayFrame(U frame, ChannelHandlerContext context) {
        // Do nothing
    }

    @Override
    public <U extends Http2Frame> void onSettingsFrame(U frame, ChannelHandlerContext context) {
        // Do nothing
    }
}
