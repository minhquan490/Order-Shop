package com.bachlinh.order.core.server.netty.channel.stomp;

public interface StompSubscriptionOperation<T> {
    void subscribe(StompSubscription stompSubscription);

    void unSubscribe(StompSubscription stompSubscription);

    void unSubscribe(String subscribeId, String destination, T channel);
}
