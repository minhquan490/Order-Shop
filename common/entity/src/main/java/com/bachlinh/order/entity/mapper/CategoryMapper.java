package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CategoryMapper extends AbstractEntityMapper<Category> {
    @Override
    protected String getTableName() {
        return Category.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Category result = getEntityFactory().getEntity(Category.class);
        result.setProducts(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CATEGORY")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Product.class);
            Set<Product> productSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("PRODUCT")) {
                    var product = mapper.map(resultSet);
                    if (product != null) {
                        product.getCategories().add(result);
                        productSet.add(product);
                    }
                }
            }
            result.setProducts(productSet);
        }
        EntityWrapper wrapper = new EntityWrapper(result);
        wrapper.setTouched(wrapped.get());
        return wrapper;
    }

    @Override
    protected void setData(Category target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CATEGORY.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CATEGORY.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CATEGORY.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CATEGORY.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CATEGORY.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CATEGORY.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
