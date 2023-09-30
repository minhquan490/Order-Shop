package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BatchReport;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class BatchReportMapper extends AbstractEntityMapper<BatchReport> {
    @Override
    protected String getTableName() {
        return BatchReport.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        BatchReport result = getEntityFactory().getEntity(BatchReport.class);
        AtomicBoolean wrappedTouched = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("BATCH_REPORT")) {
                hook = resultSet.poll();
                setData(result, hook, wrappedTouched);
            } else {
                break;
            }
        }
        EntityWrapper wrapper = new EntityWrapper(result);
        wrapper.setTouched(wrappedTouched.get());
        return wrapper;
    }

    @Override
    protected void setData(BatchReport target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "BATCH_REPORT.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.BATCH_NAME" -> {
                target.setBatchName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.HAS_ERROR" -> {
                target.setHasError((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.ERROR_DETAIL" -> {
                target.setErrorDetail((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.TIME_REPORT" -> {
                target.setTimeReport((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "BATCH_REPORT.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
