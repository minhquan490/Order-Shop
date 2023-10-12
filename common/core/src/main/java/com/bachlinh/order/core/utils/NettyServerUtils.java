package com.bachlinh.order.core.utils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.incubator.codec.http3.Http3;
import io.netty.incubator.codec.quic.InsecureQuicTokenHandler;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicSslContext;

import com.bachlinh.order.core.http.server.ssl.SslContextProvider;

import java.util.concurrent.TimeUnit;

public final class NettyServerUtils {

    private NettyServerUtils() {
    }

    public static class NettyHttp3ServerOption {
        private final ChannelHandler http3Codec;
        private final Bootstrap http3Bootstrap;

        NettyHttp3ServerOption(ChannelHandler http3Codec, Bootstrap http3Bootstrap) {
            this.http3Codec = http3Codec;
            this.http3Bootstrap = http3Bootstrap;
        }

        public Bootstrap getHttp3Bootstrap() {
            return http3Bootstrap;
        }

        public ChannelHandler getHttp3Codec() {
            return http3Codec;
        }
    }

    public static NettyHttp3ServerOption createHttp3Option(SslContextProvider sslContextProvider, ChannelInitializer<QuicChannel> initializer) {
        QuicSslContext sslContext = sslContextProvider.createQuicSsl();
        ChannelHandler codec = createHttp3Codec(sslContext, initializer);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.AUTO_CLOSE, true);
        return new NettyHttp3ServerOption(codec, bootstrap);
    }

    private static ChannelHandler createHttp3Codec(QuicSslContext sslContext, ChannelInitializer<QuicChannel> http3ServerInitializer) {
        return Http3.newQuicServerCodecBuilder()
                .sslContext(sslContext)
                .maxIdleTimeout(30, TimeUnit.SECONDS)
                .initialMaxData(10000000)
                .initialMaxStreamDataBidirectionalLocal(1000000)
                .initialMaxStreamDataBidirectionalRemote(1000000)
                .initialMaxStreamsBidirectional(100)
                .tokenHandler(InsecureQuicTokenHandler.INSTANCE)
                .handler(http3ServerInitializer)
                .build();
    }
}
