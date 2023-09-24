package com.bachlinh.order.core.server.netty.listener;

public interface HttpFrameListenerFactory<T> {
    HttpFrameListener<T> createFrameListener();
}
