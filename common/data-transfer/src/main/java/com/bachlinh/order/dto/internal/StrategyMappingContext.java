package com.bachlinh.order.dto.internal;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.MappingContext;
import com.bachlinh.order.dto.adapter.DtoStrategyInstanceAdapter;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.dto.MappingNotFoundException;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class StrategyMappingContext implements MappingContext {
    private final Map<Class<?>, DtoStrategy<?, ?>> dtoStrategyMap = new HashMap<>();

    public StrategyMappingContext(ApplicationScanner scanner, DependenciesResolver resolver, Environment environment) {
        var initializer = new DtoStrategyInstanceAdapter(scanner, resolver, environment);
        dtoStrategyMap.putAll(initializer.instanceStrategies());
    }

    @Override
    public <T, U> T map(U source, Class<T> type) throws MappingNotFoundException {
        if (!canMap(type)) {
            throw new MappingNotFoundException(String.format("Can not mapping for type [%s]. Missing strategy", type.getName()));
        }
        @SuppressWarnings("unchecked")
        DtoStrategy<T, U> strategy = (DtoStrategy<T, U>) dtoStrategyMap.get(type);
        return strategy.convert(source, type);
    }

    @Override
    public synchronized void register(Collection<?> sources) {
        var canRegister = sources.stream().allMatch(DtoStrategy.class::isInstance);
        if (canRegister) {
            sources.forEach(o -> {
                DtoStrategy<?, ?> strategy = (DtoStrategy<?, ?>) o;
                dtoStrategyMap.put(strategy.getTargetType(), strategy);
            });
        }
    }

    @Override
    public boolean canMap(Class<?> type) {
        return dtoStrategyMap.containsKey(type);
    }
}
