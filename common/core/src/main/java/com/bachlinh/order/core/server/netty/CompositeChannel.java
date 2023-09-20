package com.bachlinh.order.core.server.netty;

import io.netty.channel.Channel;

public class CompositeChannel {
    private final Channel tcpChannel;
    private final Channel udpChannel;

    public CompositeChannel(Channel tcpChannel, Channel udpChannel) {
        this.tcpChannel = tcpChannel;
        this.udpChannel = udpChannel;
    }

    public void start() throws InterruptedException {
        tcpChannel.closeFuture().sync();
        udpChannel.closeFuture().sync();
    }

    public void close() {
        tcpChannel.close();
        udpChannel.close();
    }
}
