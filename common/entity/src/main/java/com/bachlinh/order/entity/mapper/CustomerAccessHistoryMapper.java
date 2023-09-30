package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import jakarta.persistence.Table;

import java.sql.Date;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CustomerAccessHistoryMapper extends AbstractEntityMapper<CustomerAccessHistory> {
    @Override
    protected String getTableName() {
        return CustomerAccessHistory.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        CustomerAccessHistory result = getEntityFactory().getEntity(CustomerAccessHistory.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CUSTOMER_ACCESS_HISTORY")) {
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
                    customer.getHistories().add(result);
                    result.setCustomer(customer);
                }
            }
        }
        EntityWrapper wrapper = new EntityWrapper(result);
        wrapper.setTouched(wrapped.get());
        return wrapper;
    }

    @Override
    protected void setData(CustomerAccessHistory target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CUSTOMER_ACCESS_HISTORY.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_ACCESS_HISTORY.PATH_REQUEST" -> {
                target.setPathRequest((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_ACCESS_HISTORY.REQUEST_TYPE" -> {
                target.setRequestType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_ACCESS_HISTORY.REQUEST_TIME" -> {
                target.setRequestTime((Date) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_ACCESS_HISTORY.REQUEST_CONTENT" -> {
                target.setRequestContent((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_ACCESS_HISTORY.CUSTOMER_IP" -> {
                target.setCustomerIp((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER_ACCESS_HISTORY.REMOVED_TIME" -> {
                target.setRemoveTime((Date) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
