package com.bachlinh.order.core.server.netty.channel;

import com.bachlinh.order.core.http.handler.ServletHandlerAdapter;
import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.strategy.NettyHandlerContextStrategy;
import com.bachlinh.order.core.server.netty.utils.HttpUtils;
import com.bachlinh.order.exception.system.server.PayloadToLargeException;
import com.bachlinh.order.exception.system.server.RequestInvalidException;
import com.bachlinh.order.utils.JacksonUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.incubator.codec.http3.DefaultHttp3DataFrame;
import io.netty.incubator.codec.http3.DefaultHttp3HeadersFrame;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import lombok.NonNull;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final int maxHttpContentLength;
    private final ServletHandlerAdapter servletHandlerAdapter;
    private final FilterChainAdapter filterChainAdapter;

    public Http2ServerInitializer(SslContext sslCtx, ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        this(sslCtx, 16 * 1024, servletHandlerAdapter, filterChainAdapter);
    }

    public Http2ServerInitializer(SslContext sslCtx, int maxHttpContentLength, ServletHandlerAdapter servletHandlerAdapter, FilterChainAdapter filterChainAdapter) {
        if (maxHttpContentLength < 0) {
            throw new IllegalArgumentException("maxHttpContentLength (expected >= 0): " + maxHttpContentLength);
        }
        this.sslCtx = sslCtx;
        this.maxHttpContentLength = maxHttpContentLength;
        this.servletHandlerAdapter = servletHandlerAdapter;
        this.filterChainAdapter = filterChainAdapter;
    }

    @Override
    protected void initChannel(@NonNull SocketChannel ch) {
        configureSsl(ch);
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

    private void configureSsl(SocketChannel ch) {
        ch.config().setOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        ch.pipeline().addLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS));
        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), HttpUtils.getServerAPNHandler(servletHandlerAdapter, filterChainAdapter));
    }
}
