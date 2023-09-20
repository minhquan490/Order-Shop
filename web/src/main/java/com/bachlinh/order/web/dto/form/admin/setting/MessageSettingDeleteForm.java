package com.bachlinh.order.web.dto.form.admin.setting;

import com.bachlinh.order.validate.base.ValidatedDto;

public class MessageSettingDeleteForm implements ValidatedDto {
    private String id;

    public MessageSettingDeleteForm() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
