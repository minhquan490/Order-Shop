package com.bachlinh.order.handler.controller;

import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.exception.system.CriticalException;
import com.bachlinh.order.service.container.ContainerWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Objects;

class DefaultControllerManager implements ControllerManager {
    private final ControllerContext controllerContext;

    private NativeRequest<?> nativeRequest;
    private NativeResponse<?> nativeResponse;

    DefaultControllerManager(@Nullable ControllerContext controllerContext, @NonNull String profile, @NonNull ContainerWrapper wrapper) {
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
    public <T> void setNativeRequest(NativeRequest<T> request) {
        this.nativeRequest = request;
    }

    @Override
    public <T> void setNativeResponse(NativeResponse<T> response) {
        this.nativeResponse = response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> NativeRequest<T> getNativeRequest() {
        return (NativeRequest<T>) nativeRequest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> NativeResponse<T> getNativeResponse() {
        return (NativeResponse<T>) nativeResponse;
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

    private void registerControllerWithDefaultConstructor(Class<?> clazz, ContainerWrapper wrapper, String profile) {
        if (!controllerContext.contains(clazz.getName())) {
            try {
                Constructor<?> defaultConstructor = clazz.getConstructor();
                AbstractController<?, ?> instance = (AbstractController<?, ?>) defaultConstructor.newInstance();
                instance.setWrapper(wrapper, profile);
                addController(instance);
            } catch (Exception e) {
                throw new CriticalException("Can not load controller [" + clazz.getName() + "]");
            }
        }
    }
}
