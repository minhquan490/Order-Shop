package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.handler.controller.ControllerManager;

public abstract class AbstractChildRoute implements ChildRoute {
    private ControllerManager controllerManager;
    private ExceptionTranslator<NativeResponse<String>> exceptionTranslator;
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

    protected void setExceptionTranslator(ExceptionTranslator<NativeResponse<String>> exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    protected void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    protected ControllerManager getControllerManager() {
        return controllerManager;
    }

    protected ExceptionTranslator<NativeResponse<String>> getExceptionTranslator() {
        return exceptionTranslator;
    }

    protected EntityFactory getEntityFactory() {
        return entityFactory;
    }
}
