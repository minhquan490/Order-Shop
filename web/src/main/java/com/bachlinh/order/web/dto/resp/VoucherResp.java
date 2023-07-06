package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Getter
@Setter
@NoArgsConstructor
@Dto(forType = "com.bachlinh.order.entity.model.Voucher")
public class VoucherResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "name", outputJsonField = "name")
    private String name;

    @MappedDtoField(targetField = "discountPercent", outputJsonField = "discount_percent")
    private int discountPercent;

    @MappedDtoField(targetField = "timeStart.toString", outputJsonField = "time_start")
    private String timeStart;

    @MappedDtoField(targetField = "timeExpired.toString", outputJsonField = "time_end")
    private String timeEnd;

    @MappedDtoField(targetField = "voucherContent", outputJsonField = "content")
    private String content;

    @MappedDtoField(targetField = "active", outputJsonField = "is_enable")
    private boolean enable;

    @MappedDtoField(targetField = "voucherCost", outputJsonField = "cost")
    private int cost;
}
