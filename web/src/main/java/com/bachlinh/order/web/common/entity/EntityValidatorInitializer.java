package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.utils.UnsafeUtils;

class EntityValidatorInitializer implements Initializer<EntityValidator<?>> {
    @Override
    public EntityValidator<?> getObject(Class<?> type, Object... params) {
        try {
            EntityValidator<?> result = (EntityValidator<?>) UnsafeUtils.allocateInstance(type);
            result.setResolver((DependenciesResolver) params[0]);
            return result;
        } catch (Exception e) {
            throw new CriticalException(e);
        }
    }
}
