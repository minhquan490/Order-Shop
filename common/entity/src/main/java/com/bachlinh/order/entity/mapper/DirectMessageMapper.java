package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.DirectMessage;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class DirectMessageMapper extends AbstractEntityMapper<DirectMessage> {

    @Override
    protected void assignMultiTable(DirectMessage target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(DirectMessage target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return DirectMessage.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        DirectMessage result = getEntityFactory().getEntity(DirectMessage.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            assignFromCustomer(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignToCustomer(resultSet, result);
        }
        
        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(DirectMessage target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "DIRECT_MESSAGE.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DIRECT_MESSAGE.CONTENT" -> {
                target.setContent((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DIRECT_MESSAGE.SENT_TIME" -> {
                target.setTimeSent((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DIRECT_MESSAGE.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DIRECT_MESSAGE.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DIRECT_MESSAGE.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "DIRECT_MESSAGE.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }

    private void assignToCustomer(Queue<MappingObject> resultSet, DirectMessage result) {
        String toCustomerKey = "TO_CUSTOMER";
        Queue<MappingObject> cloned = new LinkedList<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals(toCustomerKey)) {
                hook = resultSet.poll();
                cloned.add(new MappingObject(hook.columnName().replace(toCustomerKey, "CUSTOMER"), hook.value()));
            } else {
                break;
            }
        }
        var mapper = getEntityMapperFactory().createMapper(Customer.class);
        var toCustomer = mapper.map(cloned);
        if (toCustomer != null) {
            result.setToCustomer(toCustomer);
        }
    }

    private void assignFromCustomer(Queue<MappingObject> resultSet, DirectMessage result) {
        Queue<MappingObject> cloned = new LinkedList<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("FROM_CUSTOMER")) {
                hook = resultSet.poll();
                cloned.add(new MappingObject(hook.columnName().replace("FROM_CUSTOMER", "CUSTOMER"), hook.value()));
            } else {
                break;
            }
        }
        var mapper = getEntityMapperFactory().createMapper(Customer.class);
        var fromCustomer = mapper.map(cloned);
        if (fromCustomer != null) {
            result.setFromCustomer(fromCustomer);
        }
    }
}
