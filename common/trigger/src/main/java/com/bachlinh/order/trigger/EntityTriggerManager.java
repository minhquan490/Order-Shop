package com.bachlinh.order.trigger;

import java.util.Collection;

import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.model.BaseEntity;

public interface EntityTriggerManager {
    <T extends BaseEntity<?>> Collection<EntityTrigger<T>> getTriggers(Class<?> entityType);

    <T extends BaseEntity<?>> void registerTrigger(EntityTrigger<T> trigger, Class<T> entityType);

    <T extends BaseEntity<?>> void unwrapTrigger(EntityTrigger<T> entityTrigger);

    <T extends BaseEntity<?>, S extends T> void executeTrigger(S entity, TriggerMode mode, TriggerExecution execution);
}
