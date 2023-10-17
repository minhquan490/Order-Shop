package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends NativeQueryRepository {

    Order saveOrder(Order order);

    Order updateOrder(Order order);

    boolean deleteOrder(Order order);

    boolean isOrderExist(String orderId);

    Order getOrder(String orderId);

    List<Order> getNewOrdersInDate();

    List<Order> getAll();

    Collection<Order> getOrdersOfCustomerForDelete(Customer owner);

    Collection<Order> getOrdersOfCustomer(String customerId, long page, long pageSize);

    void deleteOrders(Collection<Order> orders);

    Long countOrdersOfCustomer(String customerId);
}
