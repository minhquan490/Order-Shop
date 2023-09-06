package com.bachlinh.order.validate.rule;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.UnsafeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
class DefaultRuleFactory implements RuleFactory {

    @SneakyThrows
    @Override
    public <T extends ValidatedDto> ValidationRule<T> createRule(Class<ValidationRule<T>> targetType, DependenciesResolver resolver, Environment environment) {
        AbstractRule<T> validationRule = (AbstractRule<T>) UnsafeUtils.allocateInstance(targetType);
        return validationRule.getInstance(environment, resolver);
    }
}
