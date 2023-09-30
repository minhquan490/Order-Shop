package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerMedia;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CustomerMediaMapper extends AbstractEntityMapper<CustomerMedia> {
    @Override
    protected String getTableName() {
        return CustomerMedia.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        CustomerMedia result = getEntityFactory().getEntity(CustomerMedia.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CUSTOMER_MEDIA")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            if (mapper.canMap(resultSet)) {
                var customer = mapper.map(resultSet);
                if (customer != null) {
                    customer.setCustomerMedia(result);
                    result.setCustomer(customer);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(CustomerMedia target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CUSTOMER_MEDIA.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.URL" -> {
                target.setUrl((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.CONTENT_TYPE" -> {
                target.setContentType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.CONTENT_LENGTH" -> {
                target.setContentLength((Long) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_MEDIA.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
