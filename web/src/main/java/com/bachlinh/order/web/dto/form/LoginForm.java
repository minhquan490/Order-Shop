package com.bachlinh.order.web.dto.form;

import com.bachlinh.order.validate.base.ValidatedDto;

public record LoginForm(String username, String password) implements ValidatedDto {
}
