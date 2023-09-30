package com.bachlinh.order.validate.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.validate.base.ValidatedDto;
import org.springframework.lang.NonNull;

@ActiveReflection
public abstract non-sealed class AbstractRule<T extends ValidatedDto> implements ValidationRule<T> {
    private final Environment environment;
    private final DependenciesResolver resolver;
    private RepositoryManager repositoryManager;

    protected AbstractRule(Environment environment, DependenciesResolver resolver) {
        this.environment = environment;
        this.resolver = resolver;
    }

    @Override
    @NonNull
    public final ValidatedDto.ValidateResult validate(T dto) {
        injectDependencies();
        return doValidate(dto);
    }

    public abstract AbstractRule<T> getInstance(Environment environment, DependenciesResolver resolver);

    protected abstract ValidatedDto.ValidateResult doValidate(T dto);

    protected abstract void injectDependencies();

    protected Environment getEnvironment() {
        return this.environment;
    }

    protected DependenciesResolver getResolver() {
        return this.resolver;
    }

    protected <U> U resolveRepository(Class<U> repositoryType) {
        if (repositoryManager == null) {
            repositoryManager = getResolver().resolveDependencies(RepositoryManager.class);
        }
        return repositoryManager.getRepository(repositoryType);
    }
}
