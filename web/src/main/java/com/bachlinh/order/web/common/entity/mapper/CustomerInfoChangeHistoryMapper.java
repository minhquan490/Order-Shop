package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CustomerInfoChangeHistoryMapper extends AbstractEntityMapper<CustomerInfoChangeHistory> {

    @Override
    protected void assignMultiTable(CustomerInfoChangeHistory target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(CustomerInfoChangeHistory target, BaseEntity<?> mapped) {
        if (mapped instanceof Customer customer) {
            target.setCustomer(customer);
        }
    }

    @Override
    protected String getTableName() {
        return CustomerInfoChangeHistory.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        CustomerInfoChangeHistory result = getEntityFactory().getEntity(CustomerInfoChangeHistory.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(CustomerInfoChangeHistory target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.OLD_VALUE" -> {
                target.setOldValue((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.FIELD_NAME" -> {
                target.setFieldName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.TIME_UPDATE" -> {
                target.setTimeUpdate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_INFORMATION_CHANGE_HISTORY.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
