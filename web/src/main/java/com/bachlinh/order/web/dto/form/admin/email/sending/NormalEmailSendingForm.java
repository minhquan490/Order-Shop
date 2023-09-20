package com.bachlinh.order.web.dto.form.admin.email.sending;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class NormalEmailSendingForm implements ValidatedDto {

    @JsonAlias("title")
    private String title;

    @JsonAlias("content")
    private String content;

    @JsonAlias("content_type")
    private String contentType;

    @JsonAlias("to")
    private String toCustomer;

    public NormalEmailSendingForm() {
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getToCustomer() {
        return this.toCustomer;
    }

    @JsonAlias("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonAlias("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonAlias("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonAlias("to")
    public void setToCustomer(String toCustomer) {
        this.toCustomer = toCustomer;
    }
}
