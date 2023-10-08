package com.bachlinh.order.core.http.handler;

public interface Router<T, U> extends HandlerAdapter<T, U> {

    @Override
    void handleRequest(T request, U response);
}
