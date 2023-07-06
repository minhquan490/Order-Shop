package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Getter
@Setter
@NoArgsConstructor
@Dto(forType = "com.bachlinh.order.entity.model.OrderHistory")
public class OrderHistoryResp {

    @MappedDtoField(targetField = "id.toString", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "orderTime.toString", outputJsonField = "time_order")
    private String timeOrder;

    @MappedDtoField(targetField = "orderStatus", outputJsonField = "status")
    private String orderStatus;
}
