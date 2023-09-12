package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class ProductMediaMapper extends AbstractEntityMapper<ProductMedia> {
    @Override
    protected String getTableName() {
        return ProductMedia.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        ProductMedia result = getEntityFactory().getEntity(ProductMedia.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("PRODUCT_MEDIA")) {
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
                    product.getMedias().add(result);
                    result.setProduct(product);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(ProductMedia target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "PRODUCT_MEDIA.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.URL" -> {
                target.setUrl((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.CONTENT_LENGTH" -> {
                target.setContentLength((Long) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.CONTENT_TYPE" -> {
                target.setContentType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "PRODUCT_MEDIA.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
