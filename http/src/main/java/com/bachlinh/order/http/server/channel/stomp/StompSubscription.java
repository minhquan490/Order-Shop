package com.bachlinh.order.http.server.channel.stomp;

import com.google.common.base.Objects;
import io.netty.channel.Channel;

public record StompSubscription(String id, String userId, String role, String destination, Channel channel) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StompSubscription that)) return false;
        return Objects.equal(id, that.id) && Objects.equal(destination, that.destination) && Objects.equal(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, destination, channel);
    }
}
