package com.bachlinh.order.validate.rule;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.validate.base.ValidatedDto;

class DefaultRuleFactory implements RuleFactory {

    private final Initializer<AbstractRule<?>> initializer = new RuleInitializer();

    protected DefaultRuleFactory() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ValidatedDto> ValidationRule<T> createRule(Class<ValidationRule<T>> targetType, DependenciesResolver resolver, Environment environment) {
        return (ValidationRule<T>) initializer.getObject(targetType, environment, resolver);
    }
}
