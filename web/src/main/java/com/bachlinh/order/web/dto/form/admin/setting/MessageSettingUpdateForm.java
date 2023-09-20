package com.bachlinh.order.web.dto.form.admin.setting;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class MessageSettingUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("value")
    private String value;

    public MessageSettingUpdateForm() {
    }

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonAlias("value")
    public void setValue(String value) {
        this.value = value;
    }
}
