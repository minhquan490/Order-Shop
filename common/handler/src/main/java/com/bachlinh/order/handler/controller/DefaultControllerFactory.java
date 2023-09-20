package com.bachlinh.order.handler.controller;

import com.bachlinh.order.annotation.Scope;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.utils.UnsafeUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class DefaultControllerFactory implements ControllerFactory, ControllerContext {

    private final Map<Class<? extends Controller<?, ?>>, Controller<?, ?>> controllerMap = new HashMap<>(100, 0.9f);
    private final Map<String, Class<? extends Controller<?, ?>>> referencePath = new HashMap<>(100, 0.9f);

    @Override
    public <T, U> Controller<T, U> createController(Class<? extends Controller<T, U>> controllerType, Object... params) {
        if (params.length != 2) {
            return createControllerWithoutParams(controllerType);
        } else {
            return createControllerWithParams(controllerType, (ContainerWrapper) params[0], (String) params[1]);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U> Controller<T, U> createController(String url, RequestMethod requestMethod) {
        Class<? extends Controller<T, U>> controllerType = (Class<? extends Controller<T, U>>) referencePath.get(url);
        var controller = createController(controllerType);
        if (controller == null) {
            throw new ResourceNotFoundException("Url not found.", url);
        }
        if (!controller.getRequestMethod().equals(requestMethod)) {
            throw new HttpRequestMethodNotSupportedException(String.format("Method %s is not allow", requestMethod.name()));
        }
        return controller;
    }

    @SuppressWarnings("unchecked")
    public <T, U> void addController(Controller<T, U> controller) {
        controllerMap.put((Class<? extends Controller<?, ?>>) controller.getClass(), controller);
        referencePath.putIfAbsent(controller.getPath(), (Class<Controller<?, ?>>) controller.getClass());
    }

    public ControllerContext unwrap() {
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T, U> void registerControllerWithUnsafe(Class<?> clazz, ContainerWrapper wrapper, String profile) {
        if (!controllerMap.containsKey(clazz)) {
            AbstractController<T, U> instance = (AbstractController<T, U>) createController((Class<? extends Controller<T, U>>) clazz, wrapper, profile);
            addController(instance);
        }
    }

    private <T, U> Controller<T, U> cloneController(AbstractController<T, U> abstractController) {
        AbstractController<T, U> requestController = abstractController.newInstance();
        requestController.setContainerResolver((abstractController).getContainerResolver());
        requestController.setEnvironment(abstractController.getEnvironment());
        requestController.setRuleManager(abstractController.getRuleManager());
        return requestController;
    }

    @SuppressWarnings("unchecked")
    private <T, U> Controller<T, U> createControllerWithoutParams(Class<? extends Controller<T, U>> controllerType) {
        Controller<T, U> preResult = (Controller<T, U>) controllerMap.get(controllerType);
        if (preResult != null && controllerType.isAnnotationPresent(Scope.class)) {
            Scope scope = controllerType.getAnnotation(Scope.class);
            if (scope.value().equals(Scope.ControllerScope.REQUEST)) {
                return cloneController((AbstractController<T, U>) preResult);
            }
        }
        return preResult;
    }

    private <T, U> Controller<T, U> createControllerWithParams(Class<? extends Controller<T, U>> controllerType, ContainerWrapper wrapper, String profile) {
        AbstractController<T, U> instance;
        try {
            instance = (AbstractController<T, U>) UnsafeUtils.allocateInstance(controllerType);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        var actual = instance.newInstance();
        actual.setWrapper(wrapper, profile);
        return actual;
    }

    @Override
    public <T, U> Controller<T, U> getController(String path, RequestMethod requestMethod) {
        return createController(path, requestMethod);
    }

    @Override
    public Collection<Controller<?, ?>> queryAll() {
        return controllerMap.values();
    }
}
