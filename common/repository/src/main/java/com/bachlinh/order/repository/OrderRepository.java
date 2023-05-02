package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;

import java.util.List;

public interface OrderRepository {

    Order saveOrder(Order order);

    Order updateOrder(Order order);

    boolean deleteOrder(Order order);

    Order getOrder(String orderId);

    List<Order> getOrderOfCustomer(Customer customer);

    List<Order> getNewOrdersInDate();
}
