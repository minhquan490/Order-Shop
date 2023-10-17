package com.bachlinh.order.trigger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.entity.model.BaseEntity;

public abstract class AbstractTrigger<T extends BaseEntity<?>> implements EntityTrigger<T> {
    private DependenciesResolver dependenciesResolver;
    private boolean runSync = false;
    private Environment environment;
    private RunnableType runnableType;


    protected Logger log;

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected <U> U resolveDependencies(Class<U> dependenciesType) {
        return getDependenciesResolver().resolveDependencies(dependenciesType);
    }

    protected abstract void doExecute(T entity);

    protected abstract void inject();

    protected void setRunSync(boolean runSync) {
        this.runSync = runSync;
    }

    protected Environment getEnvironment() {
        return this.environment;
    }

    protected void changeConcurrentType(RunnableType type) {
        this.runnableType = type;
    }

    @Override
    public final void execute(T entity) {
        if (entity == null) {
            return;
        }
        inject();
        doExecute(entity);
    }

    @ActiveReflection
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResolver(DependenciesResolver resolver) {
        if (this.runnableType == null) {
            this.runnableType = RunnableType.ASYNC;
        }
        if (this.log == null) {
            log = LoggerFactory.getLogger(getClass());
        }
        this.dependenciesResolver = resolver;
    }

    public boolean isRunSync() {
        return runSync;
    }
}
