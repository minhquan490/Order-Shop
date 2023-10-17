package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerMedia;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.entity.model.TemporaryToken;
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
public class CustomerMapper extends AbstractEntityMapper<Customer> {

    @Override
    protected void assignMultiTable(Customer target, Collection<BaseEntity<?>> results) {
        String name = "";
        for (BaseEntity<?> result : results) {
            if (result instanceof Address address) {
                name = "address";
                address.setCustomer(target);
            }
            if (result instanceof Order order) {
                name = "order";
                order.setCustomer(target);
            }
            if (result instanceof CustomerAccessHistory accessHistory) {
                name = "customerAccessHistory";
                accessHistory.setCustomer(target);
            }
            if (result instanceof Voucher voucher) {
                name = "voucher";
                voucher.getCustomers().add(target);
            }
        }

        switch (name) {
            case "address" -> {
                Set<Address> addressSet = new LinkedHashSet<>(results.stream().map(Address.class::cast).toList());
                target.setAddresses(addressSet);
            }
            case "order" -> {
                Set<Order> orderSet = new LinkedHashSet<>(results.stream().map(Order.class::cast).toList());
                target.setOrders(orderSet);
            }
            case "customerAccessHistory" -> {
                Set<CustomerAccessHistory> customerAccessHistories = new LinkedHashSet<>(results.stream().map(CustomerAccessHistory.class::cast).toList());
                target.setHistories(customerAccessHistories);
            }
            case "voucher" -> {
                Set<Voucher> voucherSet = new LinkedHashSet<>(results.stream().map(Voucher.class::cast).toList());
                target.setAssignedVouchers(voucherSet);
            }
            default -> {/* Do nothing */}
        }
    }

    @Override
    protected void assignSingleTable(Customer target, BaseEntity<?> mapped) {
        if (mapped instanceof Cart cart) {
            cart.setCustomer(target);
            target.setCart(cart);
        }
        if (mapped instanceof RefreshToken refreshToken) {
            refreshToken.setCustomer(target);
            target.setRefreshToken(refreshToken);
        }
        if (mapped instanceof EmailTrash emailTrash) {
            emailTrash.setCustomer(target);
            target.setEmailTrash(emailTrash);
        }
        if (mapped instanceof CustomerMedia customerMedia) {
            customerMedia.setCustomer(target);
            target.setCustomerMedia(customerMedia);
        }
        if (mapped instanceof TemporaryToken token) {
            token.setAssignCustomer(target);
            target.setTemporaryToken(token);
        }
    }

    @Override
    protected String getTableName() {
        return Customer.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Customer result = getEntityFactory().getEntity(Customer.class);
        result.setAddresses(Collections.emptySet());
        result.setOrders(Collections.emptySet());
        result.setHistories(Collections.emptySet());
        result.setAssignedVouchers(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            assignCart(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignRefreshToken(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignEmailTrash(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignCustomerMedia(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignTemporaryToken(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignAddress(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignOrders(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignHistories(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignVouchers(resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(Customer target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "CUSTOMER.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.USER_NAME" -> {
                target.setUsername((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.PASSWORD" -> {
                target.setPassword((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.FIRST_NAME" -> {
                target.setFirstName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.LAST_NAME" -> {
                target.setLastName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.PHONE_NUMBER" -> {
                target.setPhoneNumber((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.EMAIL" -> {
                target.setEmail((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.GENDER" -> {
                target.setGender((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.ROLE" -> {
                target.setRole((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.ORDER_POINT" -> {
                target.setOrderPoint((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.ACTIVATED" -> {
                target.setActivated((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.ACCOUNT_NON_EXPIRED" -> {
                target.setAccountNonExpired((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.ACCOUNT_NON_LOCKED" -> {
                target.setAccountNonLocked((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.CREDENTIALS_NON_EXPIRED" -> {
                target.setCredentialsNonExpired((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.ENABLED" -> {
                target.setEnabled((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "CUSTOMER.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }

    private void assignRefreshToken(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(RefreshToken.class);
        var refreshToken = mapper.map(resultSet);
        if (refreshToken != null) {
            result.setRefreshToken(refreshToken);
            refreshToken.setCustomer(result);
        }
    }

    private void assignCart(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Cart.class);

        mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
    }

    private void assignEmailTrash(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(EmailTrash.class);

        mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
    }

    private void assignCustomerMedia(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(CustomerMedia.class);

        mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
    }

    private void assignTemporaryToken(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(TemporaryToken.class);

        mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
    }

    private void assignAddress(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Address.class);
        Set<BaseEntity<?>> addressSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, addressSet, "ADDRESS");
    }

    private void assignOrders(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Order.class);
        Set<BaseEntity<?>> orderSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, orderSet, "ORDERS");
    }

    private void assignHistories(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(CustomerAccessHistory.class);
        Set<BaseEntity<?>> historySet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, historySet, "CUSTOMER_ACCESS_HISTORY");
    }

    private void assignVouchers(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Voucher.class);
        Set<BaseEntity<?>> voucherSet = new LinkedHashSet<>();

        mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, voucherSet, "VOUCHER");
    }

}
