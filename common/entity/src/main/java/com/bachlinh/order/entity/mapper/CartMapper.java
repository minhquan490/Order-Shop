package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Product;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
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
        Cart result = getEntityFactory().getEntity(Cart.class);
        result.setCartDetails(Collections.emptyList());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            AbstractEntityMapper<?> mapper = (AbstractEntityMapper<?>) getEntityMapperFactory().createMapper(Customer.class);
            mapSingleTable(mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            assignCartDetails(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignProducts(resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void assignSingleTable(Cart target, BaseEntity<?> mapped) {
        if (mapped instanceof Customer customer) {
            customer.setCart(target);
            target.setCustomer(customer);
        }
    }

    @Override
    protected void assignMultiTable(Cart target, Collection<BaseEntity<?>> results) {
        String name = "";
        for (BaseEntity<?> result : results) {
            if (result instanceof Product product) {
                name = "product";
                product.getCarts().add(target);
            }
            if (result instanceof CartDetail cartDetail) {
                name = "cartDetail";
                cartDetail.setCart(target);
            }
        }

        switch (name) {
            case "product" -> {
                Set<Product> productSet = new LinkedHashSet<>(results.stream().map(Product.class::cast).toList());
                target.setProducts(productSet);
            }
            case "cartDetail" -> {
                Set<CartDetail> cartDetailSet = new LinkedHashSet<>(results.stream().map(CartDetail.class::cast).toList());
                target.setCartDetails(cartDetailSet);
            }
            default -> {/* Do nothing */}
        }
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
        Set<BaseEntity<?>> cartDetailSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, cartDetailSet, "CART_DETAIL");
    }

    private void assignProducts(Queue<MappingObject> resultSet, Cart result) {
        var mapper = getEntityMapperFactory().createMapper(Product.class);
        Set<BaseEntity<?>> productSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, productSet, "PRODUCT");
    }
}
