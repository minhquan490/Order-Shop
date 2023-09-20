package com.bachlinh.order.web.dto.form.admin.voucher;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class VoucherDeleteForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    public VoucherDeleteForm() {
    }

    public String getId() {
        return this.id;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }
}
