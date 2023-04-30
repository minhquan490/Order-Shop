package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.enums.RequestMethod;

import java.util.Collection;

public interface ControllerContext {
    <T, U> void addController(Controller<T, U> controller);

    <T, U> void removeController(Controller<T, U> controller);

    <T, U> Controller<T, U> getController(String path, RequestMethod requestMethod);

    Collection<Controller<?, ?>> queryAll();

    boolean contains(String controllerName);
}
