package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.core.annotation.Dto;
import com.bachlinh.order.core.annotation.MappedDtoField;

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

    public EmailTemplateFolderListResp() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTemplateCleanPolicy() {
        return this.templateCleanPolicy;
    }

    public int getTotalTemplate() {
        return this.totalTemplate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemplateCleanPolicy(String templateCleanPolicy) {
        this.templateCleanPolicy = templateCleanPolicy;
    }

    public void setTotalTemplate(int totalTemplate) {
        this.totalTemplate = totalTemplate;
    }
}
