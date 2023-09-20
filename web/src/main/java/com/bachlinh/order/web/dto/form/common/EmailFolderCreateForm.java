package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailFolderCreateForm implements ValidatedDto {

    @JsonAlias("name")
    private String name;

    public EmailFolderCreateForm() {
    }

    public String getName() {
        return this.name;
    }

    @JsonAlias("name")
    public void setName(String name) {
        this.name = name;
    }
}
