package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.model.OrderStatus;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class OrderMapper extends AbstractEntityMapper<Order> {

    @Override
    protected void assignMultiTable(Order target, Collection<BaseEntity<?>> results) {
        Set<OrderDetail> orderDetailSet = new LinkedHashSet<>();

        for (BaseEntity<?> result : results) {
            OrderDetail orderDetail = (OrderDetail) result;
            orderDetail.setOrder(target);
            orderDetailSet.add(orderDetail);
        }

        target.setOrderDetails(orderDetailSet);
    }

    @Override
    protected void assignSingleTable(Order target, BaseEntity<?> mapped) {
        if (mapped instanceof OrderStatus orderStatus) {
            target.setOrderStatus(orderStatus);
            orderStatus.setOrder(target);
        }
        if (mapped instanceof OrderHistory orderHistory) {
            orderHistory.setOrder(target);
            target.setOrderHistory(orderHistory);
        }
        if (mapped instanceof Customer customer) {
            customer.getOrders().add(target);
            target.setCustomer(customer);
        }
    }

    @Override
    protected String getTableName() {
        return Order.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Order result = getEntityFactory().getEntity(Order.class);
        result.setOrderDetails(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(OrderStatus.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(OrderHistory.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(OrderDetail.class);
            Set<BaseEntity<?>> orderDetailSet = new LinkedHashSet<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, orderDetailSet, "ORDER_DETAIL");
        }

        return wrap(result, wrapped.get());
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
