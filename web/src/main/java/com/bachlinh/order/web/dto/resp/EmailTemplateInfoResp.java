package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailTemplateInfoResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("expiry")
    private int expiryPolicy;

    @JsonProperty("total_argument")
    private int totalArgument;

    @JsonProperty("params")
    private String[] params;

    public EmailTemplateInfoResp() {
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

    public int getTotalArgument() {
        return this.totalArgument;
    }

    public String[] getParams() {
        return this.params;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("expiry")
    public void setExpiryPolicy(int expiryPolicy) {
        this.expiryPolicy = expiryPolicy;
    }

    @JsonProperty("total_argument")
    public void setTotalArgument(int totalArgument) {
        this.totalArgument = totalArgument;
    }

    @JsonProperty("params")
    public void setParams(String[] params) {
        this.params = params;
    }
}
