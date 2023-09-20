package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class RestoreEmailForm implements ValidatedDto {

    @JsonAlias("email_ids")
    private String[] emailIds;

    public RestoreEmailForm() {
    }

    public String[] getEmailIds() {
        return this.emailIds;
    }

    @JsonAlias("email_ids")
    public void setEmailIds(String[] emailIds) {
        this.emailIds = emailIds;
    }
}
