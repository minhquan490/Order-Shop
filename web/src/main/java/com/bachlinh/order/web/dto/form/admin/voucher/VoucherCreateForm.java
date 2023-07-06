package com.bachlinh.order.web.dto.form.admin.voucher;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class VoucherCreateForm implements ValidatedDto {

    @JsonAlias("name")
    private String name;

    @JsonAlias("discount_percent")
    private int discounterPercent;

    @JsonAlias("cost")
    private int cost;

    @JsonAlias("time_start")
    private String timeStart;

    @JsonAlias("time_end")
    private String timeEnd;

    @JsonAlias("content")
    private String content;

    @JsonAlias("is_enable")
    private boolean isEnable;
}
