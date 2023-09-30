package com.bachlinh.order.dto.adapter;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.UnsafeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DtoStrategyInstanceAdapter {

    private final ApplicationScanner scanner;
    private final DependenciesResolver resolver;
    private final Environment environment;
    private final Initializer<DtoStrategy<?, ?>> initializer = new DtoStrategyInitializer();

    public DtoStrategyInstanceAdapter(ApplicationScanner scanner, DependenciesResolver resolver, Environment environment) {
        this.scanner = scanner;
        this.resolver = resolver;
        this.environment = environment;
    }

    public Map<Class<?>, List<DtoStrategy<?, ?>>> instanceStrategies() {
        var result = new HashMap<Class<?>, List<DtoStrategy<?, ?>>>();
        for (var clazz : scanner.findComponents()) {
            if (DtoStrategy.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                var instance = initStrategy((Class<DtoStrategy<?, ?>>) clazz);
                result.compute(instance.getTargetType(), (aClass, dtoStrategies) -> {
                    if (dtoStrategies == null) {
                        dtoStrategies = new ArrayList<>();
                    }
                    dtoStrategies.add(instance);
                    return dtoStrategies;
                });
            }
        }
        return result;
    }

    private DtoStrategy<?, ?> initStrategy(Class<DtoStrategy<?, ?>> strategyClass) {
        return initializer.getObject(strategyClass, resolver, environment);
    }

    private static class DtoStrategyInitializer implements Initializer<DtoStrategy<?, ?>> {

        @Override
        public DtoStrategy<?, ?> getObject(Class<?> type, Object... params) {
            try {
                var dummyObject = UnsafeUtils.allocateInstance(type);
                return ((AbstractDtoStrategy<?, ?>) dummyObject).getInstance((DependenciesResolver) params[0], (Environment) params[1]);
            } catch (Exception e) {
                throw new CriticalException(e.getMessage(), e);
            }
        }
    }
}
