package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.CrawlResult;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CrawlResultMapper extends AbstractEntityMapper<CrawlResult> {

    @Override
    protected void assignMultiTable(CrawlResult target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(CrawlResult target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return CrawlResult.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        CrawlResult result = getEntityFactory().getEntity(CrawlResult.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(CrawlResult target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CRAWL_RESULT.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.SOURCE_PATH" -> {
                target.setSourcePath((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.TIME_FINISH" -> {
                target.setTimeFinish((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.RESOURCES" -> {
                target.setResources((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CRAWL_RESULT.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
