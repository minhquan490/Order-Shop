package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import jakarta.persistence.Table;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class ProductMapper extends AbstractEntityMapper<Product> {
    @Override
    protected String getTableName() {
        return Product.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Product result = getEntityFactory().getEntity(Product.class);
        result.setMedias(Collections.emptySet());
        result.setCarts(Collections.emptySet());
        result.setCategories(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("PRODUCT")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            assignMedias(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignCategories(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignCarts(resultSet, result);
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
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
        Set<ProductMedia> productMediaSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("PRODUCT_MEDIA")) {
                var productMedia = mapper.map(resultSet);
                if (productMedia != null) {
                    productMedia.setProduct(result);
                    productMediaSet.add(productMedia);
                }
            } else {
                break;
            }
        }
        result.setMedias(productMediaSet);
    }

    private void assignCategories(Queue<MappingObject> resultSet, Product result) {
        var mapper = getEntityMapperFactory().createMapper(Category.class);
        Set<Category> categorySet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CATEGORY")) {
                var category = mapper.map(resultSet);
                if (category != null) {
                    category.getProducts().add(result);
                    categorySet.add(category);
                }
            }
        }
        result.setCategories(categorySet);
    }

    private void assignCarts(Queue<MappingObject> resultSet, Product result) {
        var mapper = getEntityMapperFactory().createMapper(Cart.class);
        Set<Cart> cartSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CART")) {
                var cart = mapper.map(resultSet);
                if (cart != null) {
                    cart.getProducts().add(result);
                    cartSet.add(cart);
                }
            }
        }
        result.setCarts(cartSet);
    }
}
