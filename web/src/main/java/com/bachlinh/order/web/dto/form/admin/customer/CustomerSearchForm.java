package com.bachlinh.order.web.dto.form.admin.customer;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class CustomerSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;

    public CustomerSearchForm() {
    }

    public String getQuery() {
        return this.query;
    }

    @JsonAlias("query")
    public void setQuery(String query) {
        this.query = query;
    }
}
