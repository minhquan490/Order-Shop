package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailFolderUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("name")
    private String name;

    @JsonAlias("clean_policy")
    private int cleanPolicy;

    public EmailFolderUpdateForm() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getCleanPolicy() {
        return this.cleanPolicy;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonAlias("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAlias("clean_policy")
    public void setCleanPolicy(int cleanPolicy) {
        this.cleanPolicy = cleanPolicy;
    }
}
