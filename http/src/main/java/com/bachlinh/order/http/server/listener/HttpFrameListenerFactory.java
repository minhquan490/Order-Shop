package com.bachlinh.order.http.server.listener;

public interface HttpFrameListenerFactory<T> {
    HttpFrameListener<T> createFrameListener();
}
