package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.http.handler.RequestHandler;
import com.bachlinh.order.service.container.ContainerWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public sealed interface ControllerManager extends RequestHandler, ControllerContextHolder permits AbstractControllerManager {
    <T, U> void addController(Controller<T, U> controller);

    <T, U> void removeController(Controller<T, U> controller);

    static ControllerManager getInstance(@Nullable ControllerContext controllerContext, @NonNull String profile, @NonNull ContainerWrapper wrapper) {
        return new DefaultControllerManager(controllerContext, profile, wrapper);
    }
}
