package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;

import java.util.Collection;

public interface OrderDetailRepository {
    Collection<OrderDetail> getOrderDetailsOfOrder(Order order);

    void deleteOrderDetails(Collection<OrderDetail> orderDetails);
}
