package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@NoArgsConstructor
@Getter
@Setter
@Dto(forType = "com.bachlinh.order.entity.model.Order")
public class OrderInfoResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "deposited", outputJsonField = "is_deposited")
    private boolean deposited;

    @MappedDtoField(targetField = "timeOrder.toString", outputJsonField = "time_order")
    private String timeOrder;

    @MappedDtoField(targetField = "orderStatus.getStatus", outputJsonField = "status")
    private String orderStatus;
}
