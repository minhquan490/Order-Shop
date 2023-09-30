package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.core.annotation.Dto;
import com.bachlinh.order.core.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.MessageSetting")
public class MessageSettingResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "value", outputJsonField = "value")
    private String value;

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
