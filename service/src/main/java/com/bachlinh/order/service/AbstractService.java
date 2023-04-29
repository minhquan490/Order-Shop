package com.bachlinh.order.service;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.CriticalException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.Executor;

public abstract class AbstractService<T, U> implements BaseService<T, U> {
    private static final Class<?> dependencyContainerClass;

    static {
        Environment environment = Environment.getInstance("service");
        String containerName = environment.getProperty("dependencies.container.class.name");
        try {
            dependencyContainerClass = Class.forName(containerName);
        } catch (ClassNotFoundException e) {
            throw new CriticalException("Can not identity dependencies container class", e);
        }
    }

    private final Executor executor;
    private final Object container;

    protected AbstractService(Executor executor, Object container) {
        this.executor = executor;
        if (!dependencyContainerClass.isAssignableFrom(container.getClass())) {
            throw new CriticalException("Expected container is [" + dependencyContainerClass.getName() + "]");
        }
        this.container = container;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public final Result<T> save(Form<U> form) {
        inject();
        return new InternalResult<>(doSave(form.get()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public final Result<T> delete(Form<U> form) {
        inject();
        return new InternalResult<>(doDelete(form.get()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public final Result<T> update(Form<U> form) {
        inject();
        return new InternalResult<>(doUpdate(form.get()));
    }

    @Override
    public final Result<T> getOne(Form<U> form) {
        inject();
        return new InternalResult<>(doGetOne(form.get()));
    }

    @Override
    public final <X extends Iterable<T>> Result<X> getList(Form<U> form) {
        inject();
        return new InternalResult<>(doGetList(form.get()));
    }

    @SuppressWarnings("unchecked")
    protected <K> K getDependenciesContainer() {
        return (K) container;
    }

    protected Executor getExecutor() {
        return executor;
    }

    protected abstract T doSave(U param);

    protected abstract T doUpdate(U param);

    protected abstract T doDelete(U param);

    protected abstract T doGetOne(U param);

    protected abstract <X extends Iterable<T>> X doGetList(U param);

    protected abstract void inject();

    private record InternalResult<T>(T wappedResult) implements Result<T> {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InternalResult<?> that)) return false;
            return Objects.equals(wappedResult, that.wappedResult);
        }

        @Override
        public int hashCode() {
            return Objects.hash(wappedResult);
        }

        @Override
        public T get() {
            return wappedResult;
        }
    }
}
