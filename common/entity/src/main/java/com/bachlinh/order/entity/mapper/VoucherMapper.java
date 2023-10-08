package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Voucher;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class VoucherMapper extends AbstractEntityMapper<Voucher> {

    @Override
    protected void assignMultiTable(Voucher target, Collection<BaseEntity<?>> results) {
        Set<Customer> customerSet = new LinkedHashSet<>();

        for (BaseEntity<?> result : results) {
            Customer customer = (Customer) result;
            customer.getAssignedVouchers().add(target);
        }

        target.setCustomers(customerSet);
    }

    @Override
    protected void assignSingleTable(Voucher target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return Voucher.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Voucher result = getEntityFactory().getEntity(Voucher.class);
        result.setCustomers(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            Set<BaseEntity<?>> customerSet = new LinkedHashSet<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, customerSet, "CUSTOMER");
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(Voucher target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "VOUCHER.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.DISCOUNT_PERCENT" -> {
                target.setDiscountPercent((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.TIME_START" -> {
                target.setTimeStart((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.TIME_EXPIRED" -> {
                target.setTimeExpired((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.CONTENT" -> {
                target.setVoucherContent((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.COST" -> {
                target.setVoucherCost((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "VOUCHER.ENABLED" -> {
                target.setActive((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
