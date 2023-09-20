package com.bachlinh.order.web.dto.form.admin.email.template;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailTemplateCreateForm implements ValidatedDto {

    @JsonAlias("name")
    private String name;

    @JsonAlias("title")
    private String title;

    @JsonAlias("content")
    private String content;

    @JsonAlias("expire")
    private int totalMonthAlive;

    @JsonAlias("folder")
    private String folderId;

    @JsonAlias("params")
    private String[] params;

    public EmailTemplateCreateForm() {
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

    public int getTotalMonthAlive() {
        return this.totalMonthAlive;
    }

    public String getFolderId() {
        return this.folderId;
    }

    public String[] getParams() {
        return this.params;
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

    @JsonAlias("expire")
    public void setTotalMonthAlive(int totalMonthAlive) {
        this.totalMonthAlive = totalMonthAlive;
    }

    @JsonAlias("folder")
    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    @JsonAlias("params")
    public void setParams(String[] params) {
        this.params = params;
    }
}
