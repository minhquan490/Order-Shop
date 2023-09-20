package com.bachlinh.order.web.dto.form.admin.voucher;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class VoucherSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;

    public VoucherSearchForm() {
    }

    public String getQuery() {
        return this.query;
    }

    @JsonAlias("query")
    public void setQuery(String query) {
        this.query = query;
    }
}
