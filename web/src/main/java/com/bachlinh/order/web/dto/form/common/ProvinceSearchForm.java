package com.bachlinh.order.web.dto.form.common;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class ProvinceSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;

    public ProvinceSearchForm() {
    }

    public String getQuery() {
        return this.query;
    }

    @JsonAlias("query")
    public void setQuery(String query) {
        this.query = query;
    }
}
