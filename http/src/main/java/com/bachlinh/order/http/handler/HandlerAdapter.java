package com.bachlinh.order.http.handler;

public interface HandlerAdapter<T, U> {
    void handleRequest(T request, U response);
}
