package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class DeleteEmailInTrashForm implements ValidatedDto {

    @JsonAlias("email_ids")
    private String[] emailId;

    public DeleteEmailInTrashForm() {
    }

    public String[] getEmailId() {
        return this.emailId;
    }

    @JsonAlias("email_ids")
    public void setEmailId(String[] emailId) {
        this.emailId = emailId;
    }
}
