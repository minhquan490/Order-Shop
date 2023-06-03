package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.handler.controller.ControllerManager;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractChildRouteContext implements ChildRouteContext {
    private final Map<String, ChildRoute> childRoutes = new LinkedHashMap<>();
    private ControllerManager controllerManager;
    private ExceptionTranslator<NativeResponse<String>> exceptionTranslator;
    private EntityFactory entityFactory;

    @Override
    public ChildRoute getChild(String childName) {
        return childRoutes.get(childName);
    }

    @Override
    public void addChild(String childName, String rootPath, String fullChildPath) {
        String[] routes = fullChildPath.split("/");
        AbstractChildRoute parent = new SimpleChildRoute(null, "/".concat(routes[1]), rootPath);
        parent.setControllerManager(controllerManager);
        parent.setEntityFactory(entityFactory);
        for (int i = 2; i < routes.length; i++) {
            parent = new SimpleChildRoute(parent, "/".concat(routes[i]), null);
            parent.setControllerManager(controllerManager);
            parent.setEntityFactory(entityFactory);
        }
        childRoutes.put(childName, parent);
    }

    protected void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    protected void setExceptionTranslator(ExceptionTranslator<NativeResponse<String>> exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    protected void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    protected ExceptionTranslator<NativeResponse<String>> getExceptionTranslator() {
        return this.exceptionTranslator;
    }
}
