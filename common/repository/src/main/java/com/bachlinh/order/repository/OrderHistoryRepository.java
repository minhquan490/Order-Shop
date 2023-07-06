package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;

import java.util.Collection;

public interface OrderHistoryRepository {
    void saveOrderHistory(OrderHistory orderHistory);

    void updateOrderHistory(OrderHistory orderHistory);

    void deleteOrderHistory(OrderHistory orderHistory);

    OrderHistory getHistoryByOrder(Order order);

    Collection<OrderHistory> getHistoriesOfCustomer(Customer customer);
}
