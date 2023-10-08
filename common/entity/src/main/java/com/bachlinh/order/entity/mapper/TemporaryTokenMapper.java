package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.TemporaryToken;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class TemporaryTokenMapper extends AbstractEntityMapper<TemporaryToken> {
    @Override
    protected void assignMultiTable(TemporaryToken target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(TemporaryToken target, BaseEntity<?> mapped) {
        Customer customer = (Customer) mapped;
        customer.setTemporaryToken(target);
        target.setAssignCustomer(customer);
    }

    @Override
    protected String getTableName() {
        return TemporaryToken.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        TemporaryToken result = getEntityFactory().getEntity(TemporaryToken.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(TemporaryToken target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "TEMPORARY_TOKEN.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "TEMPORARY_TOKEN.VALUE" -> {
                target.setValue((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "TEMPORARY_TOKEN.EXPIRY_TIME" -> {
                target.setExpiryTime((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "TEMPORARY_TOKEN.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "TEMPORARY_TOKEN.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "TEMPORARY_TOKEN.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "TEMPORARY_TOKEN.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
