package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface OrderHistoryRepository extends NativeQueryRepository {
    void saveOrderHistory(OrderHistory orderHistory);

    Collection<OrderHistory> getHistoriesOfCustomer(Customer customer);

    OrderHistory getHistoryOfOrder(Order order);

    void deleteOrderHistory(OrderHistory orderHistory);
}
