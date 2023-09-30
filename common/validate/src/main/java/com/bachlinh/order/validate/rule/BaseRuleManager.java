package com.bachlinh.order.validate.rule;

import com.bachlinh.order.core.exception.http.ValidationFailureException;
import com.bachlinh.order.core.exception.system.validate.ValidationRuleNotFoundException;
import com.bachlinh.order.validate.base.ValidatedDto;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class BaseRuleManager implements RuleManager {
    private final Map<Class<?>, ValidationRule<?>> ruleContext = new HashMap<>();

    <T extends ValidatedDto> BaseRuleManager(Collection<ValidationRule<T>> dtoCollection) {
        dtoCollection.forEach(dto -> ruleContext.put(dto.applyOnType(), dto));
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ValidatedDto> ValidationRule<T> getRule(@NonNull Class<T> dtoType) throws ValidationRuleNotFoundException {
        var rule = ruleContext.get(dtoType);
        if (rule == null) {
            throw new ValidationRuleNotFoundException(String.format("Can not find rule for dto type [%s]", dtoType.getName()));
        }
        return (ValidationRule<T>) rule;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ValidatedDto> ValidatedDto.ValidateResult validate(@NonNull T dto) throws ValidationFailureException {
        ValidationRule<T> rule = (ValidationRule<T>) getRule(dto.getClass());
        return rule.validate(dto);
    }
}
