package com.bachlinh.order.validate.rule;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.UnsafeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;

class DefaultRuleFactory implements RuleFactory {

    protected DefaultRuleFactory() {
    }

    @Override
    public <T extends ValidatedDto> ValidationRule<T> createRule(Class<ValidationRule<T>> targetType, DependenciesResolver resolver, Environment environment) {
        AbstractRule<T> validationRule;
        try {
            validationRule = (AbstractRule<T>) UnsafeUtils.allocateInstance(targetType);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        return validationRule.getInstance(environment, resolver);
    }
}
