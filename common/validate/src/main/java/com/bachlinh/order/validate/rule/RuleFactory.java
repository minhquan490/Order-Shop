package com.bachlinh.order.validate.rule;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.validate.RuleInstanceFailureException;
import com.bachlinh.order.validate.base.ValidatedDto;

public interface RuleFactory {
    <T extends ValidatedDto> ValidationRule<T> createRule(Class<ValidationRule<T>> targetType, DependenciesResolver resolver, Environment environment) throws RuleInstanceFailureException;

    static RuleFactory defaultInstance() {
        return new DefaultRuleFactory();
    }
}
