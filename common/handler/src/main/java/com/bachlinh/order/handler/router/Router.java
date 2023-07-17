package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.handler.HandlerAdapter;

public interface Router<T, U> extends HandlerAdapter<T, U> {

    @Override
    void handleRequest(T request, U response);
}
