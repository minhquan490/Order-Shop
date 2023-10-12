package com.bachlinh.order.core.http.server.listener;

public interface HttpFrameListenerFactory<T> {
    HttpFrameListener<T> createFrameListener();
}
