package com.bachlinh.order.core.excecute;

import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;

public abstract class AbstractExecutor<T> implements Executor<T> {

    private final DependenciesContainerResolver containerResolver;

    protected AbstractExecutor(DependenciesContainerResolver containerResolver) {
        this.containerResolver = containerResolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void execute(BootWrapper<?> wrapper) {
        inject();
        T source;
        if (wrapper.getSource().getClass().equals(getBootObjectType())) {
            source = (T) wrapper.getSource();
        } else {
            throw new CriticalException("Expected param [" + getBootObjectType().getName() + "] but receive [" + wrapper.getSource().getClass() + "]");
        }
        doExecute(source);
    }

    protected abstract void inject();

    protected abstract void doExecute(T bootObject);

    protected DependenciesResolver getResolver() {
        return containerResolver.getDependenciesResolver();
    }

    protected Object unwrapActualContainer() {
        return containerResolver.resolveContainer();
    }
}
