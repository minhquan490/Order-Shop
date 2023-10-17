package com.bachlinh.order.validate.validator.spi;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.RepositoryManager;

/**
 * Base validator for all entities validator use in this project.
 *
 * @author Hoang Minh Quan
 */
public abstract class AbstractValidator<T extends BaseEntity<?>> implements EntityValidator<T> {
    private DependenciesResolver resolver;
    private RepositoryManager repositoryManager;

    protected DependenciesResolver getResolver() {
        return resolver;
    }

    protected <U> U resolveDependencies(Class<U> dependenciesType) {
        return getResolver().resolveDependencies(dependenciesType);
    }

    protected <U> U resolveRepository(Class<U> repositoryType) {
        return repositoryManager.getRepository(repositoryType);
    }

    protected abstract void inject();

    protected abstract ValidateResult doValidate(T entity);

    @Override
    public final ValidateResult validate(T entity) {
        lazyInitRepositoryManager();
        inject();
        return doValidate(entity);
    }

    @Override
    public final void setResolver(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    private void lazyInitRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = resolveDependencies(RepositoryManager.class);
        }
    }
}
