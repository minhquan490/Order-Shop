package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.core.annotation.Dto;
import com.bachlinh.order.core.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.OrderHistory")
public class OrderHistoryResp {

    @MappedDtoField(targetField = "id.toString", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "orderTime.toString", outputJsonField = "time_order")
    private String timeOrder;

    @MappedDtoField(targetField = "orderStatus", outputJsonField = "status")
    private String orderStatus;

    public String getId() {
        return this.id;
    }

    public String getTimeOrder() {
        return this.timeOrder;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
