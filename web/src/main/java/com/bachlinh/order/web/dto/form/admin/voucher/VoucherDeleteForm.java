package com.bachlinh.order.web.dto.form.admin.voucher;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class VoucherDeleteForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;
}
