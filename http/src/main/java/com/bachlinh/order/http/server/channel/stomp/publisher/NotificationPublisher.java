package com.bachlinh.order.http.server.channel.stomp.publisher;

public interface NotificationPublisher {
    void pushNotification(Object message);
}
