package com.bachlinh.order.validate.rule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.base.ValidatedDto;

@RequiredArgsConstructor(onConstructor_ = @ActiveReflection)
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

    protected abstract ValidatedDto.ValidateResult doValidate(T dto);

    protected abstract void injectDependencies();
}
