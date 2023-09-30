package com.bachlinh.order.dto;

import com.bachlinh.order.core.exception.system.dto.MappingNotFoundException;

import java.util.Collection;

public interface MappingContext {
    <T, U> T map(U source, Class<T> type) throws MappingNotFoundException;

    void register(Collection<?> sources);

    boolean canMap(Class<?> type);
}
