package com.bachlinh.order.core.server.netty.utils;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import com.bachlinh.order.core.http.writer.MessageWriter;
import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletRequestAdapter;
import com.bachlinh.order.core.server.netty.channel.handler.http2.Http2Handler;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.strategy.NettyHandlerContextStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

    public static void writeResp(ChannelHandlerContext ctx, NettyHandlerContextStrategy strategy, FullHttpRequest req, ServletHandlerAdapter servletHandlerAdapter, HttpServletResponse sharedResponse) {
        HttpServletRequest servletRequest = NettyServletRequestAdapter.from(req);
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        if (keepAlive) {
            sharedResponse.setHeader(HttpHeader.CONNECTION.asString(), HttpHeaderValue.KEEP_ALIVE.asString());
        }
        var responseEntity = servletHandlerAdapter.handle(servletRequest, sharedResponse);
        MessageWriter messageWriter = MessageWriter.httpMessageWriter(sharedResponse);
        messageWriter.writeHttpStatus(responseEntity.getStatusCode().value());
        messageWriter.writeHeader(responseEntity);
        messageWriter.writeMessage(responseEntity);

        strategy.apply(ctx, (NettyHttpConvention) sharedResponse, keepAlive);
    }

    public static ApplicationProtocolNegotiationHandler getServerAPNHandler(ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        return new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {

            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    ctx.pipeline()
                            .addLast(Http2FrameCodecBuilder.forServer().build(), new Http2Handler(servletHandlerAdapter, filterChainAdapter));
                    return;
                }
                throw new IllegalStateException("Protocol: " + protocol + " not supported");
            }
        };
    }
}
