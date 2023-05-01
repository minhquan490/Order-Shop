package com.bachlinh.order.trigger.spi;

import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class AbstractTrigger<T extends BaseEntity> implements EntityTrigger<T> {
    private final DependenciesResolver dependenciesResolver;
    private ThreadPoolTaskExecutor executor;
    private boolean runSync = false;

    protected AbstractTrigger(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected ThreadPoolTaskExecutor getExecutor() {
        if (executor == null) {
            this.executor = getDependenciesResolver().resolveDependencies(ThreadPoolTaskExecutor.class);
        }
        return executor;
    }

    public abstract void doExecute(T entity);

    protected abstract void inject();

    protected abstract String getTriggerName();

    @Override
    public final void execute(T entity) {
        inject();
        if (!runSync) {
            Runnable runnable = () -> doExecute(entity);
            getExecutor().execute(runnable);
        } else {
            doExecute(entity);
        }
    }

    protected void setRunSync(boolean runSync) {
        this.runSync = runSync;
    }
}
