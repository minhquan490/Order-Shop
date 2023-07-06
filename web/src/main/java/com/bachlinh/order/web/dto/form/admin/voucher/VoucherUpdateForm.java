package com.bachlinh.order.web.dto.form.admin.voucher;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class VoucherUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("name")
    private String name;

    @JsonAlias("discount_percent")
    private int discountPercent;

    @JsonAlias("time_start")
    private String timeStart;

    @JsonAlias("time_end")
    private String timeEnd;

    @JsonAlias("content")
    private String content;

    @JsonAlias("cost")
    private int cost;

    @JsonAlias("is_enabled")
    private boolean enabled;
}
