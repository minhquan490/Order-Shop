package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.Product;
import jakarta.persistence.Table;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class OrderDetailMapper extends AbstractEntityMapper<OrderDetail> {
    @Override
    protected String getTableName() {
        return OrderDetail.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        OrderDetail result = getEntityFactory().getEntity(OrderDetail.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("ORDER_DETAIL")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Product.class);
            if (mapper.canMap(resultSet)) {
                var product = mapper.map(resultSet);
                if (product != null) {
                    result.setProduct(product);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Order.class);
            if (mapper.canMap(resultSet)) {
                var order = mapper.map(resultSet);
                if (order != null) {
                    order.getOrderDetails().add(result);
                    result.setOrder(order);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(OrderDetail target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "ORDER_DETAIL.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ORDER_DETAIL.AMOUNT" -> {
                target.setAmount((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
