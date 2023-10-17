package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.LoginHistory;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class LoginHistoryMapper extends AbstractEntityMapper<LoginHistory> {

    @Override
    protected void assignMultiTable(LoginHistory target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(LoginHistory target, BaseEntity<?> mapped) {
        Customer customer = (Customer) mapped;
        target.setCustomer(customer);
    }

    @Override
    protected String getTableName() {
        return LoginHistory.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        LoginHistory result = getEntityFactory().getEntity(LoginHistory.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(LoginHistory target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "LOGIN_HISTORY.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.LAST_LOGIN_TIME" -> {
                target.setLastLoginTime((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.LOGIN_IP" -> {
                target.setLoginIp((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.SUCCESS" -> {
                target.setSuccess((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "LOGIN_HISTORY.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
