package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class AddressMapper extends AbstractEntityMapper<Address> {

    @Override
    protected void assignMultiTable(Address target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(Address target, BaseEntity<?> mapped) {
        if (mapped instanceof Customer customer) {
            target.setCustomer(customer);
            customer.getAddresses().add(target);
        }
    }

    @Override
    protected String getTableName() {
        return Address.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Address result = getEntityFactory().getEntity(Address.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }
        
        return wrap(result, wrapped.get());
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
