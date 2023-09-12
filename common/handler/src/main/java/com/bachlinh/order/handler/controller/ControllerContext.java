package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.enums.RequestMethod;

import java.util.Collection;

public interface ControllerContext {

    <T, U> Controller<T, U> getController(String path, RequestMethod requestMethod);

    Collection<Controller<?, ?>> queryAll();
}
