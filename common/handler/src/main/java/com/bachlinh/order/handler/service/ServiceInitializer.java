package com.bachlinh.order.handler.service;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.utils.UnsafeUtils;

class ServiceInitializer implements Initializer<AbstractService> {

    @Override
    public AbstractService getObject(Class<?> type, Object... params) {
        try {
            AbstractService dummy = (AbstractService) UnsafeUtils.allocateInstance(type);
            return (AbstractService) dummy.getInstance((DependenciesResolver) params[0], (Environment) params[1]);
        } catch (InstantiationException e) {
            throw new CriticalException("Can not create service [" + type.getName() + "]", e);
        }
    }
}
