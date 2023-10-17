package com.bachlinh.order.web.common.trigger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.trigger.EntityTrigger;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.VnAddress;
import com.bachlinh.order.trigger.AbstractTriggerManager;

public class ThreadScheduleEntityTriggerManager extends AbstractTriggerManager {
    private final ThreadPoolManager threadPoolManager;

    public ThreadScheduleEntityTriggerManager(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.threadPoolManager = resolver.resolveDependencies(ThreadPoolManager.class);
    }

    @Override
    public <T extends BaseEntity<?>> Collection<EntityTrigger<T>> getTriggers(Class<?> entityType) {
        Collection<EntityTrigger<T>> results = new LinkedList<>();

        List<EntityTrigger<?>> triggersApplyOnInterface = getEntityTriggerMap().get(BaseEntity.class);
        addTriggers(results, triggersApplyOnInterface);

        List<EntityTrigger<?>> triggersApplyOnEntity = getEntityTriggerMap().get(entityType);
        addTriggers(results, triggersApplyOnEntity);

        List<EntityTrigger<?>> triggersApplyOnAbstract = getEntityTriggerMap().get(AbstractEntity.class);
        addTriggers(results, triggersApplyOnAbstract);

        List<EntityTrigger<?>> triggersApplyOnVnAddress = getEntityTriggerMap().get(VnAddress.class);
        addTriggers(results, triggersApplyOnVnAddress);

        return results;
    }

    @Override
    protected <T extends BaseEntity<?>, S extends T> void doExecuteTrigger(Collection<EntityTrigger<S>> asyncTriggers, S entity) {
        Runnable task = () -> asyncTriggers.forEach(abstractTrigger -> abstractTrigger.execute(entity));

        threadPoolManager.execute(task);
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity<?>> void addTriggers(Collection<EntityTrigger<T>> results, Collection<EntityTrigger<?>> target) {
        if (target != null) {
            target.forEach(entityTrigger -> results.add((EntityTrigger<T>) entityTrigger));
        }
    }
}
