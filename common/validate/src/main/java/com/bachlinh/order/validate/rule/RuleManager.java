package com.bachlinh.order.validate.rule;

import org.springframework.lang.NonNull;
import com.bachlinh.order.exception.http.ValidationFailureException;
import com.bachlinh.order.exception.system.validate.ValidationRuleNotFoundException;
import com.bachlinh.order.validate.base.ValidatedDto;

import java.util.Collection;

public interface RuleManager {

    @NonNull
    <T extends ValidatedDto> ValidationRule<T> getRule(@NonNull Class<T> dtoType) throws ValidationRuleNotFoundException;

    @NonNull
    <T extends ValidatedDto> ValidatedDto.ValidateResult validate(@NonNull T dto) throws ValidationFailureException;

    static <T extends ValidatedDto> RuleManager getBase(Collection<ValidationRule<T>> dtoCollection) {
        return new BaseRuleManager(dtoCollection);
    }
}
