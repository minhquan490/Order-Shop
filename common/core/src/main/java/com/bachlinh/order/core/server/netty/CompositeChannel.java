package com.bachlinh.order.core.server.netty;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeChannel {
    private final Channel tcpChannel;
    private final Channel udpChannel;

    public void start() throws InterruptedException {
        tcpChannel.closeFuture().sync();
        udpChannel.closeFuture().sync();
    }

    public void close() {
        tcpChannel.close();
        udpChannel.close();
    }
}
