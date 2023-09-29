package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.utils.UnsafeUtils;

class TriggerInitializer implements Initializer<EntityTrigger<? extends BaseEntity<?>>> {
    @Override
    public EntityTrigger<? extends BaseEntity<?>> getObject(Class<?> type, Object... params) {
        AbstractTrigger<? extends BaseEntity<?>> trigger;
        try {
            trigger = (AbstractTrigger<? extends BaseEntity<?>>) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        trigger.setEnvironment((Environment) params[0]);
        trigger.setResolver((DependenciesResolver) params[1]);

        return trigger;
    }
}
