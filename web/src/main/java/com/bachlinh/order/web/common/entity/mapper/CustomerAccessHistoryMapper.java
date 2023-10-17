package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;

import jakarta.persistence.Table;

import java.sql.Date;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CustomerAccessHistoryMapper extends AbstractEntityMapper<CustomerAccessHistory> {

    @Override
    protected void assignMultiTable(CustomerAccessHistory target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(CustomerAccessHistory target, BaseEntity<?> mapped) {
        if (mapped instanceof Customer customer) {
            customer.getHistories().add(target);
            target.setCustomer(customer);
        }
    }

    @Override
    protected String getTableName() {
        return CustomerAccessHistory.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        CustomerAccessHistory result = getEntityFactory().getEntity(CustomerAccessHistory.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
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
