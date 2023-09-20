package com.bachlinh.order.core.server.netty.strategy;

import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.quic.QuicStreamChannel;

class Http3Strategy implements NettyHandlerContextStrategy {
    @Override
    public void apply(ChannelHandlerContext ctx, NettyHttpConvention convention, boolean keepAlive) {
        var header = convention.toH3HeaderFrame();
        var data = convention.toH3DataFrame();
        ctx.write(header);
        if (keepAlive) {
            ctx.write(data);
        } else {
            ctx.write(data).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
        }
        ctx.flush();
    }

    @Override
    public void apply(ChannelHandlerContext ctx, NettyHttpConvention convention) {
        apply(ctx, convention, false);
    }
}
