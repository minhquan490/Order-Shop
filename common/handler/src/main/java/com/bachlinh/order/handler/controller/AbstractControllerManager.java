package com.bachlinh.order.handler.controller;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.ContainerWrapper;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Objects;

public abstract non-sealed class AbstractControllerManager implements ControllerManager {
    private final ControllerContext controllerContext;

    protected AbstractControllerManager(@Nullable ControllerContext controllerContext, @NonNull String profile, @NonNull ContainerWrapper wrapper) {
        this.controllerContext = Objects.requireNonNullElseGet(controllerContext, DefaultControllerContext::new);
        ApplicationScanner scanner = new ApplicationScanner();
        Collection<Class<?>> controllerClasses = scanner
                .findComponents()
                .stream()
                .filter(Controller.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(RouteProvider.class))
                .toList();
        controllerClasses.forEach(clazz -> registerControllerWithDefaultConstructor(clazz, wrapper, profile));
    }

    @Override
    public <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException {
        Controller<T, U> controller = controllerContext.getController(controllerPath, method);
        controller.setNativeRequest(getNativeRequest());
        controller.setNativeResponse(getNativeResponse());
        return controller.handle(request);
    }

    @Override
    public <T, U> void addController(Controller<T, U> controller) {
        controllerContext.addController(controller);
    }

    @Override
    public <T, U> void removeController(Controller<T, U> controller) {
        controllerContext.removeController(controller);
    }

    public Collection<Controller<?, ?>> getAllController() {
        return controllerContext.queryAll();
    }

    private void registerControllerWithDefaultConstructor(Class<?> clazz, ContainerWrapper wrapper, String profile) {
        if (!controllerContext.contains(clazz.getName())) {
            try {
                Constructor<?> defaultConstructor = clazz.getConstructor();
                AbstractController<?, ?> instance = (AbstractController<?, ?>) defaultConstructor.newInstance();
                instance.setWrapper(wrapper, profile);
                addController(instance);
            } catch (Exception e) {
                throw new CriticalException("Can not load controller [" + clazz.getName() + "]", e);
            }
        }
    }
}
