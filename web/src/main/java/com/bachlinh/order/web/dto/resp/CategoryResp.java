package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.Category")
@NoArgsConstructor
@Getter
@Setter
public class CategoryResp {

    @MappedDtoField(targetField = "id", outputJsonField = "category_id")
    private String id;

    @MappedDtoField(targetField = "name", outputJsonField = "category_name")
    private String name;
}
