package com.bachlinh.order.web.dto.form;

import com.bachlinh.order.validate.base.ValidatedDto;

public record CategoryUpdateForm(String id, String name) implements ValidatedDto {

    @Override
    public String id() {
        return (id == null || id.isBlank()) ? "" : id;
    }

    @Override
    public String name() {
        return name == null ? "" : name;
    }
}
