package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class OrderHistoryMapper extends AbstractEntityMapper<OrderHistory> {
    @Override
    protected String getTableName() {
        return OrderHistory.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        OrderHistory result = getEntityFactory().getEntity(OrderHistory.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("ORDER_HISTORY")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Order.class);
            if (mapper.canMap(resultSet)) {
                var order = mapper.map(resultSet);
                if (order != null) {
                    order.setOrderHistory(result);
                    result.setOrder(order);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(OrderHistory target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "ORDER_HISTORY.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_HISTORY.ORDER_TIME" -> {
                target.setOrderTime((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_HISTORY.ORDER_STATUS" -> {
                target.setOrderStatus((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_HISTORY.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_HISTORY.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_HISTORY.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_HISTORY.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
