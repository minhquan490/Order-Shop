package com.bachlinh.order.validate.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.base.ValidatedDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@ActiveReflection
public abstract non-sealed class AbstractRule<T extends ValidatedDto> implements ValidationRule<T> {
    private final Environment environment;
    private final DependenciesResolver resolver;

    @Override
    @NonNull
    public final ValidatedDto.ValidateResult validate(T dto) {
        injectDependencies();
        return doValidate(dto);
    }

    public abstract AbstractRule<T> getInstance(Environment environment, DependenciesResolver resolver);

    protected abstract ValidatedDto.ValidateResult doValidate(T dto);

    protected abstract void injectDependencies();
}
