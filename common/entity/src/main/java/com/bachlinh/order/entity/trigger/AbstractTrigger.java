package com.bachlinh.order.entity.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTrigger<T extends BaseEntity<?>> implements EntityTrigger<T> {
    private DependenciesResolver dependenciesResolver;
    private ThreadPoolManager threadPoolManager;
    private boolean runSync = false;
    private Environment environment;
    private RunnableType runnableType;
    private RepositoryManager repositoryManager;

    protected Logger log;

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected ThreadPoolManager getExecutor() {
        if (threadPoolManager == null) {
            this.threadPoolManager = resolveDependencies(ThreadPoolManager.class);
        }
        return threadPoolManager;
    }

    protected <U> U resolveDependencies(Class<U> dependenciesType) {
        return getDependenciesResolver().resolveDependencies(dependenciesType);
    }

    protected <U> U resolveRepository(Class<U> repositoryType) {
        if (repositoryManager == null) {
            repositoryManager = resolveDependencies(RepositoryManager.class);
        }
        return repositoryManager.getRepository(repositoryType);
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
}
