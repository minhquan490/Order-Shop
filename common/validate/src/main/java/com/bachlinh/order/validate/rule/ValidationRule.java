package com.bachlinh.order.validate.rule;

import com.bachlinh.order.exception.http.ValidationFailureException;
import com.bachlinh.order.validate.base.ValidatedDto;

public sealed interface ValidationRule<T extends ValidatedDto> permits AbstractRule {
    ValidatedDto.ValidateResult validate(T dto) throws ValidationFailureException;

    Class<T> applyOnType();
}
