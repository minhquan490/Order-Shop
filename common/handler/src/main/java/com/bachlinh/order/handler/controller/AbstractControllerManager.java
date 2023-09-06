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
import com.bachlinh.order.utils.UnsafeUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Objects;

public abstract non-sealed class AbstractControllerManager implements ControllerManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ControllerContext controllerContext;
    private ObjectInterceptor interceptor;

    protected AbstractControllerManager(@Nullable ControllerContext controllerContext, @NonNull String profile, @NonNull ContainerWrapper wrapper) {
        this.controllerContext = Objects.requireNonNullElseGet(controllerContext, DefaultControllerContext::new);
        ApplicationScanner scanner = new ApplicationScanner();
        Collection<Class<?>> controllerClasses = scanner
                .findComponents()
                .stream()
                .filter(Controller.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(RouteProvider.class))
                .toList();
        controllerClasses.forEach(clazz -> registerControllerWithUnsafe(clazz, wrapper, profile));
        try {
            interceptor = DependenciesContainerResolver.buildResolver(wrapper.unwrap(), profile).getDependenciesResolver().resolveDependencies(ObjectInterceptor.class);
        } catch (Exception e) {
            logger.info("No interceptor available");
            interceptor = null;
        }
    }

    @Override
    public <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException {
        Controller<T, U> controller = controllerContext.getController(controllerPath, method);
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
    public <T, U> void addController(Controller<T, U> controller) {
        controllerContext.addController(controller);
    }

    @Override
    public <T, U> void removeController(Controller<T, U> controller) {
        controllerContext.removeController(controller);
    }

    @Override
    public ControllerContext getContext() {
        return controllerContext;
    }

    public Collection<Controller<?, ?>> getAllController() {
        return controllerContext.queryAll();
    }

    @SneakyThrows
    private void registerControllerWithUnsafe(Class<?> clazz, ContainerWrapper wrapper, String profile) {
        if (!controllerContext.contains(clazz.getName())) {
            AbstractController<?, ?> instance = (AbstractController<?, ?>) UnsafeUtils.allocateInstance(clazz);
            var actualInstance = instance.newInstance();
            actualInstance.setWrapper(wrapper, profile);
            addController(actualInstance);
        }
    }
}
