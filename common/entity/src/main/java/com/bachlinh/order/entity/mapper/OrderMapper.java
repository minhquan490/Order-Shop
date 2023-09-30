package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.model.OrderStatus;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class OrderMapper extends AbstractEntityMapper<Order> {
    @Override
    protected String getTableName() {
        return Order.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Order result = getEntityFactory().getEntity(Order.class);
        result.setOrderDetails(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("ORDERS")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(OrderStatus.class);
            if (mapper.canMap(resultSet)) {
                var orderStatus = mapper.map(resultSet);
                if (orderStatus != null) {
                    orderStatus.setOrder(result);
                    result.setOrderStatus(orderStatus);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(OrderHistory.class);
            if (mapper.canMap(resultSet)) {
                var orderHistory = mapper.map(resultSet);
                if (orderHistory != null) {
                    orderHistory.setOrder(result);
                    result.setOrderHistory(orderHistory);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            if (mapper.canMap(resultSet)) {
                var customer = mapper.map(resultSet);
                if (customer != null) {
                    customer.getOrders().add(result);
                    result.setCustomer(customer);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(OrderDetail.class);
            Set<OrderDetail> orderDetailSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ORDER_DETAIL")) {
                    var orderDetails = mapper.map(resultSet);
                    if (orderDetails != null) {
                        orderDetails.setOrder(result);
                        orderDetailSet.add(orderDetails);
                    }
                } else {
                    break;
                }
            }
            result.setOrderDetails(orderDetailSet);
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(Order target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "ORDERS.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDERS.ORDER_TIME" -> {
                target.setTimeOrder((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDERS.BANK_TRANSACTION_CODE" -> {
                target.setBankTransactionCode((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
