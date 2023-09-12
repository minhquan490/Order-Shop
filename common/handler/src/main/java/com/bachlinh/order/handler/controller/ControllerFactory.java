package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.enums.RequestMethod;

public interface ControllerFactory {
    <T, U> Controller<T, U> createController(Class<? extends Controller<T, U>> controllerType, Object... params);

    <T, U> Controller<T, U> createController(String url, RequestMethod requestMethod);
}
