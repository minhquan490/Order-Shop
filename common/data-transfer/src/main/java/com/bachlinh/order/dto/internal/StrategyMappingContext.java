package com.bachlinh.order.dto.internal;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.MappingContext;
import com.bachlinh.order.dto.adapter.DtoStrategyInstanceAdapter;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.DtoProxyConvertException;
import com.bachlinh.order.exception.system.dto.MappingNotFoundException;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StrategyMappingContext implements MappingContext {
    private final Map<Class<?>, List<DtoStrategy<?, ?>>> dtoStrategyMap = new HashMap<>();

    public StrategyMappingContext(ApplicationScanner scanner, DependenciesResolver resolver, Environment environment) {
        var initializer = new DtoStrategyInstanceAdapter(scanner, resolver, environment);
        dtoStrategyMap.putAll(initializer.instanceStrategies());
    }

    @Override
    public <T, U> T map(U source, Class<T> type) throws MappingNotFoundException {
        var strategies = dtoStrategyMap.get(type);
        if (!canMap(type) || strategies == null || strategies.isEmpty()) {
            throw new MappingNotFoundException(String.format("Can not mapping for type [%s]. Missing strategy", type.getName()));
        } else {
            for (var strategy : strategies) {
                var targetType = ((ParameterizedType) strategy.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
                var targetClass = (Class<?>) targetType;
                if (targetClass.isAssignableFrom(source.getClass())) {
                    @SuppressWarnings("unchecked")
                    var castedStrategy = (DtoStrategy<T, U>) strategy;
                    return castedStrategy.convert(source, type);
                }
            }
        }
        throw new DtoProxyConvertException("Missing strategy, so can not convert");
    }

    @Override
    public synchronized void register(Collection<?> sources) {
        var canRegister = sources.stream().allMatch(DtoStrategy.class::isInstance);
        if (canRegister) {
            sources.forEach(o -> {
                DtoStrategy<?, ?> strategy = (DtoStrategy<?, ?>) o;
                dtoStrategyMap.compute(strategy.getTargetType(), (aClass, dtoStrategies) -> {
                    if (dtoStrategies == null) {
                        dtoStrategies = new ArrayList<>();
                    }
                    dtoStrategies.add(strategy);
                    return dtoStrategies;
                });
            });

        }
    }

    @Override
    public boolean canMap(Class<?> type) {
        return dtoStrategyMap.containsKey(type);
    }
}
