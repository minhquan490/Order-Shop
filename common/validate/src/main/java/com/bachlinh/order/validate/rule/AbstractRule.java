package com.bachlinh.order.validate.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.base.ValidatedDto;
import org.springframework.lang.NonNull;

@ActiveReflection
public abstract non-sealed class AbstractRule<T extends ValidatedDto> implements ValidationRule<T> {
    private final Environment environment;
    private final DependenciesResolver resolver;

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
}
