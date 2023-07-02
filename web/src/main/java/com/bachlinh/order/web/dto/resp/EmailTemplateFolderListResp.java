package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Getter
@Setter
@NoArgsConstructor
@Dto(forType = "com.bachlinh.order.entity.model.EmailTemplateFolder")
public class EmailTemplateFolderListResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "name", outputJsonField = "name")
    private String name;

    @MappedDtoField(targetField = "clearTemplatePolicy.toString", outputJsonField = "template_clean_policy")
    private String templateCleanPolicy;
    
    @MappedDtoField(targetField = "emailTemplates.size", outputJsonField = "total_template")
    private int totalTemplate;
}
