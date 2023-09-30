package com.bachlinh.order.validate.rule;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.UnsafeUtils;

class RuleInitializer implements Initializer<AbstractRule<?>> {
    @Override
    public AbstractRule<?> getObject(Class<?> type, Object... params) {
        AbstractRule<?> validationRule;
        try {
            validationRule = (AbstractRule<?>) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        return validationRule.getInstance((Environment) params[0], (DependenciesResolver) params[1]);
    }
}
