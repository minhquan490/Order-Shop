package com.bachlinh.order.dto.strategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;

@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract non-sealed class AbstractDtoStrategy<T, U> implements DtoStrategy<T, U> {
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;

    @Override
    public final T convert(U source, Class<T> type) {
        beforeConvert(source, type);
        var result = doConvert(source, type);
        afterConvert(source, type);
        return result;
    }

    protected abstract void beforeConvert(U source, Class<T> type);

    protected abstract T doConvert(U source, Class<T> type);

    protected abstract void afterConvert(U source, Class<T> type);
}
