package com.bachlinh.order.core.http.handler;

public interface HandlerAdapter<T, U> {
    void handleRequest(T request, U response);
}
