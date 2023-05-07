package com.bachlinh.order.core.excecute;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;

public abstract class AbstractExecutor<T> implements Executor<T> {

    private final DependenciesContainerResolver containerResolver;
    private final Environment environment;

    protected AbstractExecutor(DependenciesContainerResolver containerResolver, String profile) {
        this.containerResolver = containerResolver;
        this.environment = Environment.getInstance(profile);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void execute(BootWrapper<?> wrapper) {
        inject();
        T source = (T) wrapper.getSource();
        if (source != null) {
            if (!getBootObjectType().isAssignableFrom(source.getClass())) {
                if (!getBootObjectType().equals(source.getClass())) {
                    throw new CriticalException("Expected param [" + getBootObjectType().getName() + "] but receive [" + wrapper.getSource().getClass() + "]");
                }
            }
        }
        doExecute(source);
    }

    protected abstract void inject();

    protected abstract void doExecute(T bootObject);

    protected DependenciesResolver getResolver() {
        return containerResolver.getDependenciesResolver();
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected Object unwrapActualContainer() {
        return containerResolver.resolveContainer();
    }
}
