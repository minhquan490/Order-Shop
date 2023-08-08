package com.bachlinh.order.web.dto.form.customer;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public record RegisterForm(@JsonAlias("email") String email,
                           @JsonAlias("password") String password) implements ValidatedDto {
}
