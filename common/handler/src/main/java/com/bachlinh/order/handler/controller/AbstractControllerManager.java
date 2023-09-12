package com.bachlinh.order.handler.controller;

import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.handler.interceptor.spi.ObjectInterceptor;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import org.springframework.lang.NonNull;

import java.util.Collection;

public abstract non-sealed class AbstractControllerManager implements ControllerManager {
    private final ControllerFactory controllerFactory;
    private ObjectInterceptor interceptor;

    protected AbstractControllerManager(@NonNull String profile, @NonNull ContainerWrapper wrapper) {
        this.controllerFactory = new DefaultControllerFactory();
        ApplicationScanner scanner = new ApplicationScanner();
        Collection<Class<?>> controllerClasses = scanner
                .findComponents()
                .stream()
                .filter(Controller.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(RouteProvider.class))
                .toList();
        controllerClasses.forEach(clazz -> {
            DefaultControllerFactory defaultControllerFactory = (DefaultControllerFactory) controllerFactory;
            defaultControllerFactory.registerControllerWithUnsafe(clazz, wrapper, profile);
        });
        try {
            interceptor = DependenciesContainerResolver.buildResolver(wrapper.unwrap(), profile).getDependenciesResolver().resolveDependencies(ObjectInterceptor.class);
        } catch (Exception e) {
            interceptor = null;
        }
    }

    @Override
    public <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException {
        Controller<T, U> controller = controllerFactory.createController(controllerPath, method);
        controller.setNativeRequest(getNativeRequest());
        controller.setNativeResponse(getNativeResponse());
        NativeResponse<T> nativeResponse = null;
        if (interceptor == null) {
            return controller.handle(request);
        }
        try {
            if (!interceptor.shouldHandle(controller.getNativeRequest(), controller.getNativeResponse())) {
                return controller.getNativeResponse();
            }
            nativeResponse = controller.handle(request);
            interceptor.afterHandle(getNativeRequest(), nativeResponse);
            return nativeResponse;
        } finally {
            interceptor.onCompletion(getNativeRequest(), nativeResponse);
        }
    }

    @Override
    public ControllerContext getContext() {
        return ((DefaultControllerFactory) controllerFactory).unwrap();
    }
}
