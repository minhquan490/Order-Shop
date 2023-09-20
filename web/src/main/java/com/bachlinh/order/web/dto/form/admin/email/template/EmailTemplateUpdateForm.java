package com.bachlinh.order.web.dto.form.admin.email.template;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailTemplateUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("name")
    private String name;

    @JsonAlias("title")
    private String title;

    @JsonAlias("content")
    private String content;

    @JsonAlias("expiry")
    private int expiryPolicy;

    @JsonAlias("params")
    private String[] params;

    @JsonAlias("folder_id")
    private String folderId;

    public EmailTemplateUpdateForm() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public int getExpiryPolicy() {
        return this.expiryPolicy;
    }

    public String[] getParams() {
        return this.params;
    }

    public String getFolderId() {
        return this.folderId;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonAlias("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAlias("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonAlias("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonAlias("expiry")
    public void setExpiryPolicy(int expiryPolicy) {
        this.expiryPolicy = expiryPolicy;
    }

    @JsonAlias("params")
    public void setParams(String[] params) {
        this.params = params;
    }

    @JsonAlias("folder_id")
    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}
