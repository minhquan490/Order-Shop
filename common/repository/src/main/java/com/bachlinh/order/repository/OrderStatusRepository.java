package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderStatus;

public interface OrderStatusRepository {
    void deleteStatus(OrderStatus status);

    OrderStatus getStatusOfOrder(Order order);
}
