package com.bachlinh.order.web.dto.form.admin;

import com.bachlinh.order.validate.base.ValidatedDto;

public record CategoryCreateForm(String name) implements ValidatedDto {
}
