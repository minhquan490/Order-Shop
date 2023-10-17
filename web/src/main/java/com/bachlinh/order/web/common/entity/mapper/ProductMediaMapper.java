package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class ProductMediaMapper extends AbstractEntityMapper<ProductMedia> {

    @Override
    protected void assignMultiTable(ProductMedia target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(ProductMedia target, BaseEntity<?> mapped) {
        Product product = (Product) mapped;
        product.getMedias().add(target);
        target.setProduct(product);
    }

    @Override
    protected String getTableName() {
        return ProductMedia.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        ProductMedia result = getEntityFactory().getEntity(ProductMedia.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Product.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
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
