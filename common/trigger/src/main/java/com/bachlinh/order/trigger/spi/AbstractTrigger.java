package com.bachlinh.order.trigger.spi;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;

public abstract class AbstractTrigger<T extends BaseEntity> implements EntityTrigger<T> {
    private final DependenciesResolver dependenciesResolver;
    private ThreadPoolTaskExecutor executor;
    private boolean runSync = false;
    private Environment environment;

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

    protected abstract void doExecute(T entity);

    protected abstract void inject();

    protected abstract String getTriggerName();

    protected void setRunSync(boolean runSync) {
        this.runSync = runSync;
    }

    protected Environment getEnvironment() {
        return this.environment;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void execute(T entity) {
        inject();
        if (!runSync) {
            Runnable runnable = () -> doExecute(entity);
            getExecutor().execute(runnable);
        } else {
            doExecute(entity);
        }
    }

    @ActiveReflection
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
