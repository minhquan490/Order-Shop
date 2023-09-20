package com.bachlinh.order.web.dto.form.admin.email.template;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailTemplateSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;

    public EmailTemplateSearchForm() {
    }

    public String getQuery() {
        return this.query;
    }

    @JsonAlias("query")
    public void setQuery(String query) {
        this.query = query;
    }
}
