package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.core.annotation.Dto;
import com.bachlinh.order.core.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.Province")
public class ProvinceResp {

    @MappedDtoField(targetField = "id.toString", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "name", outputJsonField = "name")
    private String name;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
