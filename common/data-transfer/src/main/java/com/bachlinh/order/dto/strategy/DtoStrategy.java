package com.bachlinh.order.dto.strategy;

public sealed interface DtoStrategy<T, U> permits AbstractDtoStrategy {
    T convert(U source, Class<T> type);

    Class<T> getTargetType();
}
