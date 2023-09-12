package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.RefreshToken;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class RefreshTokenMapper extends AbstractEntityMapper<RefreshToken> {
    @Override
    protected String getTableName() {
        return RefreshToken.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        RefreshToken result = getEntityFactory().getEntity(RefreshToken.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("REFRESH_TOKEN")) {
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
                    customer.setRefreshToken(result);
                    result.setCustomer(customer);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(RefreshToken target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "REFRESH_TOKEN.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.TIME_CREATED" -> {
                target.setTimeCreated((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.TIME_EXPIRED" -> {
                target.setTimeExpired((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.VALUE" -> {
                target.setRefreshTokenValue((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "REFRESH_TOKEN.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
