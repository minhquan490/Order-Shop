package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;

public record LoginForm(String username, String password) implements ValidatedDto {
}
