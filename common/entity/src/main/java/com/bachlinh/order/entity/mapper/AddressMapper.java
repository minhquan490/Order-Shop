package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Customer;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class AddressMapper extends AbstractEntityMapper<Address> {

    @Override
    protected String getTableName() {
        return Address.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Address result = getEntityFactory().getEntity(Address.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("ADDRESS")) {
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
                    result.setCustomer(customer);
                }
            }
        }
        EntityWrapper wrapper = new EntityWrapper(result);
        wrapper.setTouched(wrapped.get());
        return wrapper;
    }


    @Override
    protected void setData(Address target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "ADDRESS.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.VALUE" -> {
                target.setValue((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.CITY" -> {
                target.setCity((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.COUNTRY" -> {
                target.setCountry((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "ADDRESS.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
