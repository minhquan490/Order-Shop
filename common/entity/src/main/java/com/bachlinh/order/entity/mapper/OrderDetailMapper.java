package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.Product;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class OrderDetailMapper extends AbstractEntityMapper<OrderDetail> {

    @Override
    protected void assignMultiTable(OrderDetail target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(OrderDetail target, BaseEntity<?> mapped) {
        if (mapped instanceof Product product) {
            target.setProduct(product);
        }
        if (mapped instanceof Order order) {
            order.getOrderDetails().add(target);
            target.setOrder(order);
        }
    }

    @Override
    protected String getTableName() {
        return OrderDetail.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        OrderDetail result = getEntityFactory().getEntity(OrderDetail.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Product.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Order.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }
        
        return wrap(result, wrapped.get());
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
