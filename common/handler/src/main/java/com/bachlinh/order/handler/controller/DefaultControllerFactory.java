package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.Scope;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class DefaultControllerFactory implements ControllerFactory, ControllerContext {

    private final Map<Class<? extends Controller<?, ?>>, Controller<?, ?>> controllerMap = new HashMap<>(100, 0.9f);
    private final Map<String, Class<? extends Controller<?, ?>>> referencePath = new HashMap<>(100, 0.9f);
    private final Initializer<AbstractController<?, ?>> initializer = new ControllerInitializer();

    @Override
    public <T, U> Controller<T, U> createController(Class<? extends Controller<T, U>> controllerType, Object... params) {
        if (params.length != 2) {
            return createControllerWithoutParams(controllerType);
        } else {
            // Unsafe init controller for first creation
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

    public <T, U> void addController(Controller<T, U> controller, Class<? extends Controller<?, ?>> controllerType) {
        controllerMap.put(controllerType, controller);
        referencePath.putIfAbsent(controller.getPath(), controllerType);
    }

    public ControllerContext unwrap() {
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T, U> void registerControllerWithUnsafe(Class<?> clazz, ContainerWrapper wrapper, String profile) {
        if (!controllerMap.containsKey(clazz)) {
            AbstractController<T, U> instance = (AbstractController<T, U>) createController((Class<? extends Controller<T, U>>) clazz, wrapper, profile);
            addController(instance, (Class<? extends Controller<?, ?>>) clazz);
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
        if (preResult != null && isControllerAnnotatedScope(controllerType)) {
            Scope scope = controllerType.getAnnotation(Scope.class);
            if (scope.value().equals(Scope.ControllerScope.SINGLETON)) {
                return preResult;
            }
        } else {
            return clone((AbstractController<T, U>) preResult);
        }
        return preResult;
    }

    @SuppressWarnings("unchecked")
    private <T, U> Controller<T, U> createControllerWithParams(Class<? extends Controller<T, U>> controllerType, ContainerWrapper wrapper, String profile) {
        return (Controller<T, U>) initializer.getObject(controllerType, wrapper, profile);
    }

    @Override
    public <T, U> Controller<T, U> getController(String path, RequestMethod requestMethod) {
        return createController(path, requestMethod);
    }

    @Override
    public Collection<Controller<?, ?>> queryAll() {
        return controllerMap.values();
    }

    private <T, U> Controller<T, U> clone(AbstractController<T, U> preResult) {
        if (preResult == null) {
            throw new ResourceNotFoundException("Not found", "");
        }
        return cloneController(preResult);
    }

    private boolean isControllerAnnotatedScope(Class<?> controllerType) {
        Class<?> parent = (Class<?>) ((ParameterizedType) controllerType.getGenericSuperclass()).getRawType();
        return controllerType.isAnnotationPresent(Scope.class) && parent.isAnnotationPresent(Scope.class);
    }
}
