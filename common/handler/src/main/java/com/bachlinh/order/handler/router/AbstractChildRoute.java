package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;

public abstract class AbstractChildRoute implements ChildRoute {
    private ControllerManager controllerManager;
    private EntityFactory entityFactory;

    @Override
    public <T> void setNativeResponse(NativeResponse<T> response) {
        controllerManager.setNativeResponse(response);
    }

    @Override
    public <T> void setNativeRequest(NativeRequest<T> request) {
        controllerManager.setNativeRequest(request);
    }

    @Override
    public <T> NativeResponse<T> getNativeResponse() {
        return controllerManager.getNativeResponse();
    }

    @Override
    public <T> NativeRequest<T> getNativeRequest() {
        return controllerManager.getNativeRequest();
    }

    protected void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    protected void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    protected ControllerManager getControllerManager() {
        return controllerManager;
    }

    protected EntityFactory getEntityFactory() {
        return entityFactory;
    }

    protected Environment getEnvironment() {
        return Environment.getInstance(entityFactory.activeProfile());
    }
}
