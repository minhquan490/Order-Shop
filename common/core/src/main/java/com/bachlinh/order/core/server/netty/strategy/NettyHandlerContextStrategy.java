package com.bachlinh.order.core.server.netty.strategy;

import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import io.netty.channel.ChannelHandlerContext;

public interface NettyHandlerContextStrategy {
    void apply(ChannelHandlerContext ctx, NettyHttpConvention convention, boolean keepAlive);

    void apply(ChannelHandlerContext ctx, NettyHttpConvention convention);

    static NettyHandlerContextStrategy getHttp2Strategy() {
        return new Http2Strategy();
    }

    static NettyHandlerContextStrategy getHttp3Strategy() {
        return new Http3Strategy();
    }
}
