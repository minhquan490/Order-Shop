package com.bachlinh.order.trigger.spi;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractTrigger<T extends BaseEntity<?>> implements EntityTrigger<T> {
    private final DependenciesResolver dependenciesResolver;
    private ThreadPoolManager threadPoolManager;
    private boolean runSync = false;
    private Environment environment;
    private RunnableType runnableType = RunnableType.ASYNC;

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected AbstractTrigger(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected ThreadPoolManager getExecutor() {
        if (threadPoolManager == null) {
            this.threadPoolManager = getDependenciesResolver().resolveDependencies(ThreadPoolManager.class);
        }
        return threadPoolManager;
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
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void execute(T entity) {
        inject();
        if (!runSync) {
            Runnable runnable = () -> doExecute(entity);
            getExecutor().execute(runnable, runnableType);
        } else {
            doExecute(entity);
        }
    }

    @ActiveReflection
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
