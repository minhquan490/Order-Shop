package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.OrderHistory;

import java.util.Collection;

public interface OrderHistoryRepository extends NativeQueryRepository {
    void saveOrderHistory(OrderHistory orderHistory);

    Collection<OrderHistory> getHistoriesOfCustomer(Customer customer);
}
