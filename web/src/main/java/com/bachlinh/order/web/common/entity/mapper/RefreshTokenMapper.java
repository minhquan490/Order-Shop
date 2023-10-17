package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.RefreshToken;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class RefreshTokenMapper extends AbstractEntityMapper<RefreshToken> {
    @Override
    protected void assignMultiTable(RefreshToken target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(RefreshToken target, BaseEntity<?> mapped) {
        Customer customer = (Customer) mapped;
        customer.setRefreshToken(target);
        target.setCustomer(customer);
    }

    @Override
    protected String getTableName() {
        return RefreshToken.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        RefreshToken result = getEntityFactory().getEntity(RefreshToken.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
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
