package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderStatus;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class OrderStatusMapper extends AbstractEntityMapper<OrderStatus> {

    @Override
    protected void assignMultiTable(OrderStatus target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(OrderStatus target, BaseEntity<?> mapped) {
        Order order = (Order) mapped;
        order.setOrderStatus(target);
        target.setOrder(order);
    }

    @Override
    protected String getTableName() {
        return OrderStatus.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        OrderStatus result = getEntityFactory().getEntity(OrderStatus.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Order.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(OrderStatus target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "ORDER_STATUS.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_STATUS.STATUS" -> {
                target.setStatus((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_STATUS.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_STATUS.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_STATUS.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_STATUS.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
