package com.bachlinh.order.core.server.netty.channel.stomp;

public interface StompConnectionManager<T> extends StompSubscriptionOperation<T>, StompSubscriptionHolder {
    int totalClientConnected();

    void sendMessage(String destination, String toCustomer, Object message);

    void sendMessage(String destination, Object message);
}
