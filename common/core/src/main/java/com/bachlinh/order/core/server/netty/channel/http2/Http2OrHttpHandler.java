package com.bachlinh.order.core.server.netty.channel.http2;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.server.netty.Http2CompressFrameCodecBuilder;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListenerFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class Http2OrHttpHandler extends ApplicationProtocolNegotiationHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int MAX_CONTENT_LENGTH = 1024 * 100;
    private final HttpFrameListenerFactory<Http2Frame> listenerFactory;

    @SuppressWarnings("unchecked")
    public Http2OrHttpHandler(DependenciesResolver resolver) {
        super(ApplicationProtocolNames.HTTP_1_1);
        this.listenerFactory = (HttpFrameListenerFactory<Http2Frame>) resolver.resolveDependencies("http2FrameListener");
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            Http2CompressFrameCodecBuilder builder = new Http2CompressFrameCodecBuilder();
            Http2FrameCodec frameCodec = builder.build();
            ctx.pipeline().addLast(frameCodec, new DefaultHttp2Handler(listenerFactory));
            return;
        }
        if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            ctx.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(MAX_CONTENT_LENGTH), new DefaultHttp2Handler(listenerFactory));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            super.handlerRemoved(ctx);
        } catch (IllegalStateException e) {
            // Do nothing
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error cause when handle request, close connection", cause);
        ctx.close();
    }
}
