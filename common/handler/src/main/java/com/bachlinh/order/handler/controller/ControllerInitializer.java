package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.UnsafeUtils;

class ControllerInitializer implements Initializer<AbstractController<?, ?>> {
    @Override
    public AbstractController<?, ?> getObject(Class<?> type, Object... params) {
        AbstractController<?, ?> instance;
        try {
            instance = (AbstractController<?, ?>) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        AbstractController<?, ?> actual = instance.newInstance();
        actual.setWrapper((ContainerWrapper) params[0], (String) params[1]);
        return actual;
    }
}
