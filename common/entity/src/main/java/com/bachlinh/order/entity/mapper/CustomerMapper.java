package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Address;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class CustomerMapper extends AbstractEntityMapper<Customer> {
    @Override
    protected String getTableName() {
        return Customer.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        Customer result = getEntityFactory().getEntity(Customer.class);
        result.setAddresses(Collections.emptySet());
        result.setOrders(Collections.emptySet());
        result.setHistories(Collections.emptySet());
        result.setAssignedVouchers(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CUSTOMER")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
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
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
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
        if (mapper.canMap(resultSet)) {
            var cart = mapper.map(resultSet);
            if (cart != null) {
                result.setCart(cart);
                cart.setCustomer(result);
            }
        }
    }

    private void assignEmailTrash(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(EmailTrash.class);
        if (mapper.canMap(resultSet)) {
            var emailTrash = mapper.map(resultSet);
            if (emailTrash != null) {
                result.setEmailTrash(emailTrash);
                emailTrash.setCustomer(result);
            }
        }
    }

    private void assignCustomerMedia(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(CustomerMedia.class);
        if (mapper.canMap(resultSet)) {
            var customerMedia = mapper.map(resultSet);
            if (customerMedia != null) {
                result.setCustomerMedia(customerMedia);
                customerMedia.setCustomer(result);
            }
        }
    }

    private void assignTemporaryToken(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(TemporaryToken.class);
        if (mapper.canMap(resultSet)) {
            var temporaryToken = mapper.map(resultSet);
            if (temporaryToken != null) {
                result.setTemporaryToken(temporaryToken);
                temporaryToken.setAssignCustomer(result);
            }
        }
    }

    private void assignAddress(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Address.class);
        Set<Address> addressSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("ADDRESS")) {
                var address = mapper.map(resultSet);
                if (address != null) {
                    address.setCustomer(result);
                    addressSet.add(address);
                }
            } else {
                break;
            }
        }
        result.setAddresses(addressSet);
    }

    private void assignOrders(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Order.class);
        Set<Order> orderSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("ORDERS")) {
                var order = mapper.map(resultSet);
                if (order != null) {
                    order.setCustomer(result);
                    orderSet.add(order);
                }
            } else {
                break;
            }
        }
        result.setOrders(orderSet);
    }

    private void assignHistories(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(CustomerAccessHistory.class);
        Set<CustomerAccessHistory> historySet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("CUSTOMER_ACCESS_HISTORY")) {
                var customerAccessHistory = mapper.map(resultSet);
                if (customerAccessHistory != null) {
                    customerAccessHistory.setCustomer(result);
                    historySet.add(customerAccessHistory);
                }
            } else {
                break;
            }
        }
        result.setHistories(historySet);
    }

    private void assignVouchers(Queue<MappingObject> resultSet, Customer result) {
        var mapper = getEntityMapperFactory().createMapper(Voucher.class);
        Set<Voucher> voucherSet = new LinkedHashSet<>();
        while (!resultSet.isEmpty()) {
            MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("VOUCHER")) {
                var voucher = mapper.map(resultSet);
                if (voucher != null) {
                    voucher.getCustomers().add(result);
                    voucherSet.add(voucher);
                }
            } else {
                break;
            }
        }
        result.setAssignedVouchers(voucherSet);
    }

}
