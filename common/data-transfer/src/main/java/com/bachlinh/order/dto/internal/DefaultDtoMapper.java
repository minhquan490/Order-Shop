package com.bachlinh.order.dto.internal;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.dto.MappingNotFoundException;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.dto.MappingContext;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultDtoMapper implements DtoMapper {
    private MappingContext proxyContext;
    private MappingContext strategiesContext;

    @Override
    public <T, U> T map(U source, Class<T> type) throws MappingNotFoundException {
        if (proxyContext.canMap(type)) {
            return mapWithProxy(source, type);
        }
        if (strategiesContext.canMap(type)) {
            return mapWithStrategy(source, type);
        }

        String message = STR. "Can not map type [\{ type.getName() }]. Missing proxy and strategy" ;
        throw new MappingNotFoundException(message);
    }

    @Override
    public <T, U> Collection<T> map(Collection<U> sources, Class<T> type) throws MappingNotFoundException {
        var results = new ArrayList<T>(sources.size());
        for (var source : sources) {
            var mapped = map(source, type);
            results.add(mapped);
        }
        return results;
    }

    @Override
    public <T, U> T mapWithProxy(U source, Class<T> type) throws MappingNotFoundException {
        if (!proxyContext.canMap(type)) {
            String message = STR. "Can not map type [\{ type.getName() }]. Missing proxy" ;
            throw new MappingNotFoundException(message);
        }
        return proxyContext.map(source, type);
    }

    @Override
    public <T, U> Collection<T> mapWithProxy(Collection<U> sources, Class<T> type) throws MappingNotFoundException {
        var results = new ArrayList<T>(sources.size());
        sources.forEach(u -> results.add(mapWithProxy(u, type)));
        return results;
    }

    @Override
    public <T, U> T mapWithStrategy(U source, Class<T> type) throws MappingNotFoundException {
        if (!strategiesContext.canMap(type)) {
            String message = STR. "Can not map type [\{ type.getName() }]. Missing strategy" ;
            throw new MappingNotFoundException(message);
        }
        return strategiesContext.map(source, type);
    }

    @Override
    public <T, U> Collection<T> mapWithStrategy(Collection<U> sources, Class<T> type) throws MappingNotFoundException {
        var results = new ArrayList<T>(sources.size());
        sources.forEach(u -> results.add(mapWithStrategy(u, type)));
        return results;
    }

    @Override
    public DtoMapper initialize(ApplicationScanner scanner, DependenciesResolver resolver, Environment environment) {
        this.proxyContext = new ProxyMappingContext();
        this.strategiesContext = new StrategyMappingContext(scanner, resolver, environment);
        return this;
    }

    @Override
    public void destroy() {
        this.proxyContext = null;
        this.strategiesContext = null;
    }
}
