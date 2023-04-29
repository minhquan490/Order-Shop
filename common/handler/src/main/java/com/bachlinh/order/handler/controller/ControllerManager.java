package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.http.handler.RequestHandler;

public interface ControllerManager extends RequestHandler {
    <T, U> void addController(Controller<T, U> controller);

    <T, U> void removeController(Controller<T, U> controller);
}
