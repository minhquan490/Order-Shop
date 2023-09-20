package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class WardSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;

    public WardSearchForm() {
    }

    public String getQuery() {
        return this.query;
    }

    @JsonAlias("query")
    public void setQuery(String query) {
        this.query = query;
    }
}
