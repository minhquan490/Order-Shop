package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.LoginHistory;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class LoginHistoryMapper extends AbstractEntityMapper<LoginHistory> {
    @Override
    protected String getTableName() {
        return LoginHistory.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        LoginHistory result = getEntityFactory().getEntity(LoginHistory.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("LOGIN_HISTORY")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            var customer = mapper.map(resultSet);
            if (customer != null) {
                result.setCustomer(customer);
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
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
