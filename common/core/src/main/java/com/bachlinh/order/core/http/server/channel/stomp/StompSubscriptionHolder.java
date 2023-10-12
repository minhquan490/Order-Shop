package com.bachlinh.order.core.http.server.channel.stomp;

import java.util.List;

public interface StompSubscriptionHolder {
    List<StompSubscription> getSubscriptions(String destination);
}
