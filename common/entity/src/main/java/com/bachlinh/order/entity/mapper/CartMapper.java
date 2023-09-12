package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Product;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CartMapper extends AbstractEntityMapper<Cart> {
    @Override
    protected String getTableName() {
        return Cart.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Cart result = getEntityFactory().getEntity(Cart.class);
        result.setCartDetails(Collections.emptyList());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CART")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            if (mapper.canMap(resultSet)) {
                var customer = mapper.map(resultSet);
                if (customer != null) {
                    customer.setCart(result);
                    result.setCustomer(customer);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            assignCartDetails(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignProducts(resultSet, result);
        }
        EntityWrapper wrapper = new EntityWrapper(result);
        wrapper.setTouched(wrapped.get());
        return wrapper;
    }

    @Override
    protected void setData(Cart target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CART.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CART.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }

    private void assignCartDetails(Queue<MappingObject> resultSet, Cart result) {
        var mapper = getEntityMapperFactory().createMapper(CartDetail.class);
        Set<CartDetail> cartDetailSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CART_DETAIL")) {
                var cartDetail = mapper.map(resultSet);
                if (cartDetail != null) {
                    cartDetail.setCart(result);
                    cartDetailSet.add(cartDetail);
                }
            } else {
                break;
            }
        }
        result.setCartDetails(cartDetailSet);
    }

    private void assignProducts(Queue<MappingObject> resultSet, Cart result) {
        var mapper = getEntityMapperFactory().createMapper(Product.class);
        Set<Product> productSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("PRODUCT")) {
                var product = mapper.map(resultSet);
                if (product != null) {
                    product.getCarts().add(result);
                    productSet.add(product);
                }
            } else {
                break;
            }
        }
        result.setProducts(productSet);
    }
}
