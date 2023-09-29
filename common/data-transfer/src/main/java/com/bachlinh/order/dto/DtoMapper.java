package com.bachlinh.order.dto;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.internal.DefaultDtoMapper;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.dto.MappingNotFoundException;
import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.Collection;

public interface DtoMapper {
    <T, U> T map(U source, Class<T> type) throws MappingNotFoundException;

    <T, U> Collection<T> map(Collection<U> sources, Class<T> type) throws MappingNotFoundException;

    <T, U> T mapWithProxy(U source, Class<T> type) throws MappingNotFoundException;

    <T, U> Collection<T> mapWithProxy(Collection<U> sources, Class<T> type) throws MappingNotFoundException;

    <T, U> T mapWithStrategy(U source, Class<T> type) throws MappingNotFoundException;

    <T, U> Collection<T> mapWithStrategy(Collection<U> sources, Class<T> type) throws MappingNotFoundException;

    DtoMapper initialize(ApplicationScanner scanner, DependenciesResolver resolver, Environment environment);

    void destroy();

    static DtoMapper defaultInstance(ApplicationScanner applicationScanner, DependenciesResolver resolver, Environment environment) {
        return new DefaultDtoMapper().initialize(applicationScanner, resolver, environment);
    }
}
