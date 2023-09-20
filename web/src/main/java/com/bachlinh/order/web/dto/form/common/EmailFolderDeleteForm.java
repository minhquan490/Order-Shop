package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailFolderDeleteForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    public EmailFolderDeleteForm() {
    }

    public String getId() {
        return this.id;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }
}
