package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.Order")
public class OrderListResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "deposited", outputJsonField = "is_deposited")
    private boolean deposited;

    @MappedDtoField(targetField = "timeOrder.toString", outputJsonField = "time_order")
    private String timeOrder;

    @MappedDtoField(targetField = "orderStatus.getStatus", outputJsonField = "status")
    private String orderStatus;

    @MappedDtoField(targetField = "customer.getUsername", outputJsonField = "customer_name")
    private String customerName;

    public OrderListResp() {
    }

    public String getId() {
        return this.id;
    }

    public boolean isDeposited() {
        return this.deposited;
    }

    public String getTimeOrder() {
        return this.timeOrder;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeposited(boolean deposited) {
        this.deposited = deposited;
    }

    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
