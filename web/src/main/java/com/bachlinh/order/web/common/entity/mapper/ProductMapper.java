package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;

import jakarta.persistence.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class ProductMapper extends AbstractEntityMapper<Product> {

    @Override
    protected void assignMultiTable(Product target, Collection<BaseEntity<?>> results) {
        String name = "";

        for (BaseEntity<?> result : results) {
            if (result instanceof ProductMedia productMedia) {
                name = "productMedia";
                productMedia.setProduct(target);
            }
            if (result instanceof Category category) {
                name = "category";
                category.getProducts().add(target);
            }
            if (result instanceof Cart cart) {
                name = "cart";
                cart.getProducts().add(target);
            }
        }

        switch (name) {
            case "productMedia" -> {
                Set<ProductMedia> productMediaSet = new LinkedHashSet<>(results.stream().map(ProductMedia.class::cast).toList());
                target.setMedias(productMediaSet);
            }
            case "category" -> {
                Set<Category> categorySet = new LinkedHashSet<>(results.stream().map(Category.class::cast).toList());
                target.setCategories(categorySet);
            }
            case "cart" -> {
                Set<Cart> cartSet = new LinkedHashSet<>(results.stream().map(Cart.class::cast).toList());
                target.setCarts(cartSet);
            }
            default -> {/* Do nothing */}
        }
    }

    @Override
    protected void assignSingleTable(Product target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return Product.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Product result = getEntityFactory().getEntity(Product.class);
        result.setMedias(Collections.emptySet());
        result.setCarts(Collections.emptySet());
        result.setCategories(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            assignMedias(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignCategories(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignCarts(resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(Product target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "PRODUCT.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.PRICE" -> {
                target.setPrice((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.SIZE" -> {
                target.setSize((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.COLOR" -> {
                target.setColor((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.TAO_BAO_URL" -> {
                target.setTaobaoUrl((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.DESCRIPTION" -> {
                target.setDescription((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.ORDER_POINT" -> {
                target.setOrderPoint((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT.ENABLED" -> {
                target.setEnabled((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }

    private void assignMedias(Queue<MappingObject> resultSet, Product result) {
        var mapper = getEntityMapperFactory().createMapper(ProductMedia.class);
        Set<BaseEntity<?>> productMediaSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, productMediaSet, "PRODUCT_MEDIA");
    }

    private void assignCategories(Queue<MappingObject> resultSet, Product result) {
        var mapper = getEntityMapperFactory().createMapper(Category.class);
        Set<BaseEntity<?>> categorySet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, categorySet, "CATEGORY");
    }

    private void assignCarts(Queue<MappingObject> resultSet, Product result) {
        var mapper = getEntityMapperFactory().createMapper(Cart.class);
        Set<BaseEntity<?>> cartSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, cartSet, "CART");
    }
}
