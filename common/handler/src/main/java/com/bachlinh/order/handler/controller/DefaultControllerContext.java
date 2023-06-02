package com.bachlinh.order.handler.controller;

import lombok.extern.slf4j.Slf4j;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.exception.system.common.CriticalException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
class DefaultControllerContext implements ControllerContext {

    private final Map<String, Controller<?, ?>> controllerMap = new LinkedHashMap<>();

    @Override
    public <T, U> void addController(Controller<T, U> controller) {
        controllerMap.putIfAbsent(controller.getPath(), controller);
    }

    @Override
    public <T, U> void removeController(Controller<T, U> controller) {
        controllerMap.remove(controller.getPath(), controller);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U> Controller<T, U> getController(String path, RequestMethod requestMethod) {
        try {
            Controller<T, U> controller = (Controller<T, U>) controllerMap.get(path);
            if (controller == null) {
                throw new ResourceNotFoundException("Request path [" + path + "] not found");
            }
            if (controller.getRequestMethod().equals(requestMethod)) {
                if (log.isDebugEnabled()) {
                    log.debug("Found controller matching with url [{}]", path);
                }
                return controller;
            } else {
                throw new HttpRequestMethodNotSupportedException(requestMethod.name() + " is not allow");
            }
        } catch (ClassCastException e) {
            throw new CriticalException("Obtain controller failure", e);
        }
    }

    @Override
    public Collection<Controller<?, ?>> queryAll() {
        return controllerMap.values();
    }

    @Override
    public boolean contains(String controllerName) {
        return queryAll().stream().anyMatch(controller -> controller.getClass().getName().equals(controllerName));
    }
}
