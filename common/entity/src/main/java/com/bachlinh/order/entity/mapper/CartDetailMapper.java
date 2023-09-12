package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.Product;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CartDetailMapper extends AbstractEntityMapper<CartDetail> {
    @Override
    protected String getTableName() {
        return CartDetail.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        CartDetail result = getEntityFactory().getEntity(CartDetail.class);
        AtomicBoolean wrapped = new AtomicBoolean(true);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CART_DETAIL")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            assignProduct(result, resultSet);
        }
        if (!resultSet.isEmpty()) {
            assignCart(result, resultSet);
        }
        EntityWrapper wrapper = new EntityWrapper(result);
        wrapper.setTouched(wrapped.get());
        return wrapper;
    }

    @Override
    protected void setData(CartDetail target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CART_DETAIL.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART_DETAIL.AMOUNT" -> {
                target.setAmount((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART_DETAIL.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART_DETAIL.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART_DETAIL.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART_DETAIL.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }

    private void assignProduct(CartDetail result, Queue<MappingObject> resultSet) {
        var mapper = getEntityMapperFactory().createMapper(Product.class);
        if (mapper.canMap(resultSet)) {
            var product = mapper.map(resultSet);
            if (product != null) {
                result.setProduct(product);
            }
        }
    }

    private void assignCart(CartDetail result, Queue<MappingObject> resultSet) {
        var mapper = getEntityMapperFactory().createMapper(Cart.class);
        if (mapper.canMap(resultSet)) {
            var cart = mapper.map(resultSet);
            if (cart != null) {
                cart.getCartDetails().add(result);
                result.setCart(cart);
            }
        }
    }
}
