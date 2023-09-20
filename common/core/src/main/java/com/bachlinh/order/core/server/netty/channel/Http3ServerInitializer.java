package com.bachlinh.order.core.server.netty.channel;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import com.bachlinh.order.core.server.netty.channel.handler.http3.Http3Handler;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.strategy.NettyHandlerContextStrategy;
import com.bachlinh.order.exception.system.server.PayloadToLargeException;
import com.bachlinh.order.exception.system.server.RequestInvalidException;
import com.bachlinh.order.utils.JacksonUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.incubator.codec.http3.DefaultHttp3DataFrame;
import io.netty.incubator.codec.http3.DefaultHttp3HeadersFrame;
import io.netty.incubator.codec.http3.Http3ServerConnectionHandler;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Http3ServerInitializer extends ChannelInitializer<QuicChannel> {
    private final ServletHandlerAdapter servletHandlerAdapter;
    private final FilterChainAdapter filterChainAdapter;

    public Http3ServerInitializer(ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        this.servletHandlerAdapter = servletHandlerAdapter;
        this.filterChainAdapter = filterChainAdapter;
    }

    @Override
    protected void initChannel(QuicChannel ch) {
        ch.pipeline().addLast(new Http3ServerConnectionHandler(
                new ChannelInitializer<QuicStreamChannel>() {
                    @Override
                    protected void initChannel(QuicStreamChannel ch) {
                        ch.pipeline().addLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new Http3Handler(servletHandlerAdapter, filterChainAdapter));
                    }
                }
        ));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof PayloadToLargeException e) {
            sendPayloadExceptionResp(e, ctx);
            return;
        }
        if (cause instanceof ReadTimeoutException) {
            ctx.disconnect();
            return;
        }
        if (cause instanceof RequestInvalidException requestInvalidException) {
            catchRequestInvalid(requestInvalidException, ctx);
            return;
        }
        ctx.fireExceptionCaught(cause);
    }

    private void catchRequestInvalid(RequestInvalidException requestInvalidException, ChannelHandlerContext ctx) {
        var attributes = requestInvalidException.getAtrributes();
        if (attributes == null) {
            ctx.disconnect();
        } else {
            NettyHttpConvention convention = (NettyHttpConvention) attributes.get("convention");
            NettyHandlerContextStrategy strategy = (NettyHandlerContextStrategy) attributes.get("strategy");
            if (convention != null && strategy != null) {
                strategy.apply(ctx, convention);
            } else {
                ctx.disconnect();
            }
        }
    }

    private void sendPayloadExceptionResp(PayloadToLargeException e, ChannelHandlerContext ctx) {
        Map<String, Object> resp = new HashMap<>(2);
        resp.put("status", HttpStatus.PAYLOAD_TOO_LARGE_413);
        resp.put("messages", new String[]{e.getMessage()});
        byte[] dataByte = JacksonUtils.writeObjectAsBytes(resp);
        var header = new DefaultHttp3HeadersFrame();
        header.headers().status(String.valueOf(HttpStatus.PAYLOAD_TOO_LARGE_413));
        header.headers().addInt(HttpHeader.CONTENT_LENGTH.asString(), dataByte.length);
        header.headers().add(HttpHeader.CONTENT_TYPE.asString(), MediaType.APPLICATION_JSON_VALUE);
        ctx.write(header);
        ctx.write(new DefaultHttp3DataFrame(Unpooled.wrappedBuffer(dataByte))).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
        ctx.flush();
    }
}
