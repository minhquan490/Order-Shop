package com.bachlinh.order.web.dto.form;

public record CategoryUpdateForm(String id, String name) {

    @Override
    public String id() {
        return (id == null || id.isBlank()) ? "" : id;
    }

    @Override
    public String name() {
        return name == null ? "" : name;
    }
}
