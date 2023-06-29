package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;

import java.util.List;

public interface OrderRepository extends NativeQueryRepository {

    Order saveOrder(Order order);

    Order updateOrder(Order order);

    boolean deleteOrder(Order order);

    boolean isOrderExist(String orderId);

    Order getOrder(String orderId);

    List<Order> getOrderOfCustomer(Customer customer);

    List<Order> getNewOrdersInDate();

    List<Order> getAll();
}
