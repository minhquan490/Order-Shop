package com.bachlinh.order.dto.adapter;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class DtoStrategyInstanceAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ApplicationScanner scanner;
    private final DependenciesResolver resolver;
    private final Environment environment;

    public Map<Class<?>, DtoStrategy<?, ?>> instanceStrategies() {
        return this.scanner
                .findComponents()
                .stream()
                .filter(DtoStrategy.class::isAssignableFrom)
                .map(clazz -> {
                    @SuppressWarnings("unchecked")
                    Class<DtoStrategy<?, ?>> strategyClass = (Class<DtoStrategy<?, ?>>) clazz;
                    return initStrategy(strategyClass);
                })
                .collect(Collectors.toMap(DtoStrategy::getTargetType, dtoStrategy -> dtoStrategy));
    }

    private DtoStrategy<?, ?> initStrategy(Class<DtoStrategy<?, ?>> strategyClass) {
        try {
            var constructor = strategyClass.getDeclaredConstructor(DependenciesResolver.class, Environment.class);
            constructor.setAccessible(true);
            return constructor.newInstance(resolver, environment);
        } catch (Exception e) {
            log.error("Can not init instance for class [{}]", strategyClass.getName(), e);
            throw new CriticalException(e.getMessage(), e);
        }
    }
}
