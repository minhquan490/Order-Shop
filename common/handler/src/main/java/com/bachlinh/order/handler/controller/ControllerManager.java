package com.bachlinh.order.handler.controller;

import com.bachlinh.order.http.handler.RequestHandler;
import com.bachlinh.order.core.container.ContainerWrapper;
import org.springframework.lang.NonNull;

public sealed interface ControllerManager extends RequestHandler, ControllerContextHolder permits AbstractControllerManager {

    static ControllerManager getInstance(@NonNull String profile, @NonNull ContainerWrapper wrapper) {
        return new DefaultControllerManager(profile, wrapper);
    }
}
