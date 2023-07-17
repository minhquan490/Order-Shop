package com.bachlinh.order.core.server.netty.strategy;

import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import lombok.Setter;

@Setter
public class Http2Strategy implements NettyHandlerContextStrategy {

    private Http2HeadersFrame headersFrame;
    private Http2DataFrame dataFrame;

    @Override
    public void apply(ChannelHandlerContext ctx, NettyHttpConvention convention, boolean keepAlive) {
        if (headersFrame != null) {
            ctx.write(convention.toH2HeaderFrame().stream(headersFrame.stream()));
        } else {
            ctx.write(convention.toH2HeaderFrame());
        }
        if (keepAlive) {
            if (dataFrame != null) {
                ctx.write(convention.toH2DataFrame().stream(dataFrame.stream()));
            } else {
                ctx.write(convention.toH2DataFrame());
            }
        } else {
            if (dataFrame != null) {
                ctx.write(convention.toH2DataFrame().stream(dataFrame.stream())).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.write(convention.toH2DataFrame()).addListener(ChannelFutureListener.CLOSE);
            }
        }
        ctx.flush();
    }

    @Override
    public void apply(ChannelHandlerContext ctx, NettyHttpConvention convention) {
        apply(ctx, convention, false);
    }
}
