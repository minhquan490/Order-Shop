package com.bachlinh.order.web.dto.form.admin.setting;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class MessageSettingCreateForm implements ValidatedDto {

    @JsonAlias("oldValue")
    private String value;

    public MessageSettingCreateForm() {
    }

    public String getValue() {
        return this.value;
    }

    @JsonAlias("oldValue")
    public void setValue(String value) {
        this.value = value;
    }
}
