package com.bachlinh.order.trigger;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.utils.UnsafeUtils;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import com.bachlinh.order.entity.model.BaseEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

public abstract class AbstractTriggerManager implements EntityTriggerManager {

    private final MultiValueMap<Class<?>, EntityTrigger<?>> entityTriggerMap = new LinkedMultiValueMap<>();

    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity<?>, U extends EntityTrigger<T>> AbstractTriggerManager(DependenciesResolver resolver, Environment environment) {
        ApplicationScanner scanner = new ApplicationScanner();
        scanner.findComponents()
                .stream()
                .filter(this::isTrigger)
                .map(clazz -> (Class<U>) clazz)
                .map(triggerClass -> initTrigger(triggerClass, resolver, environment))
                .toList()
                .forEach(trigger -> {
                    ApplyOn applyOn = trigger.getClass().getAnnotation(ApplyOn.class);
                    Class<T> entityType = (Class<T>) applyOn.entity();
                    registerTrigger(trigger, entityType);
                });
    }

    @Override
    public final <T extends BaseEntity<?>> void registerTrigger(EntityTrigger<T> trigger, Class<T> entityType) {
        entityTriggerMap.compute(entityType, (unused, entityTriggers) -> {
            if (entityTriggers == null) {
                entityTriggers = new LinkedList<>();
            }
            if (!entityTriggers.contains(trigger)) {
                entityTriggers.add(trigger);
            }
            entityTriggers.sort(Comparator.comparing(entityTrigger -> {
                ApplyOn applyOn = entityTrigger.getClass().getAnnotation(ApplyOn.class);
                return applyOn.order();
            }));
            return entityTriggers;
        });
    }

    @Override
    public final synchronized <T extends BaseEntity<?>> void unwrapTrigger(EntityTrigger<T> entityTrigger) {
        entityTriggerMap.values().forEach(entityTriggers -> entityTriggers.remove(entityTrigger));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T extends BaseEntity<?>, S extends T> void executeTrigger(S entity, TriggerMode mode, TriggerExecution execution) {
        Class<S> entityType = (Class<S>) entity.getClass();
        Collection<EntityTrigger<S>> triggers = getTriggers(entityType);
        Collection<AbstractTrigger<S>> asyncTriggers = new LinkedList<>();

        for (EntityTrigger<S> trigger : triggers) {
            if (trigger.getMode().equals(mode) && Arrays.asList(trigger.getExecuteOn()).contains(execution)) {
                if (trigger instanceof AbstractTrigger<S> abstractTrigger && !abstractTrigger.isRunSync()) {
                    asyncTriggers.add(abstractTrigger);
                } else {
                    trigger.execute(entity);
                }
            }
        }

        doExecuteTrigger(new LinkedList<>(asyncTriggers), entity);
    }

    protected MultiValueMap<Class<?>, EntityTrigger<?>> getEntityTriggerMap() {
        return entityTriggerMap;
    }

    protected abstract <T extends BaseEntity<?>, S extends T> void doExecuteTrigger(Collection<EntityTrigger<S>> asyncTriggers, S entity);

    private boolean isTrigger(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApplyOn.class) &&
                EntityTrigger.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T extends EntityTrigger<? extends BaseEntity<?>>> T initTrigger(Class<T> triggerClass, DependenciesResolver dependenciesResolver, Environment environment) {
        Initializer<EntityTrigger<? extends BaseEntity<?>>> initializer = new TriggerInitializer();
        return (T) initializer.getObject(triggerClass, environment, dependenciesResolver);
    }

    private static class TriggerInitializer implements Initializer<EntityTrigger<? extends BaseEntity<?>>> {

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
}
