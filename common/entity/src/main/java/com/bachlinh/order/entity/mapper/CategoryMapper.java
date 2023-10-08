package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Category;
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
public class CategoryMapper extends AbstractEntityMapper<Category> {

    @Override
    protected void assignMultiTable(Category target, Collection<BaseEntity<?>> results) {
        Set<Product> productSet = new LinkedHashSet<>(results.stream().map(Product.class::cast).toList());
        target.setProducts(productSet);
    }

    @Override
    protected void assignSingleTable(Category target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return Category.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Category result = getEntityFactory().getEntity(Category.class);
        result.setProducts(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Product.class);
            Set<BaseEntity<?>> productSet = new LinkedHashSet<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, productSet, "PRODUCT");
        }

        return wrap(result, wrapped.get());
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
