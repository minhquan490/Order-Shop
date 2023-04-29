package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.query.Condition;
import com.bachlinh.order.repository.query.Join;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public interface CustomerRepository {
    AtomicInteger customerCount = new AtomicInteger(0);

    Customer getCustomerById(String id, boolean useOnFilter);

    Customer getCustomerByUsername(String username);

    Customer getCustomerByEmail(String email);

    Customer getCustomerUseJoin(Object customerId, Collection<Join> joins);

    Customer getCustomerByPhone(String phone);

    Customer saveCustomer(@NonNull Customer customer);

    boolean deleteCustomer(@NonNull Customer customer);

    boolean usernameExist(String username);

    boolean phoneNumberExist(String phone);

    boolean emailExist(String email);

    boolean existById(Object customerId);

    Customer updateCustomer(@NonNull Customer customer);

    Page<Customer> getAll(@Nullable Pageable pageable, @Nullable Sort sort);

    Page<Customer> getCustomersUsingJoin(Collection<Join> joins, Collection<Condition> conditions, @Nullable Pageable pageable, @Nullable Sort sort);

    void saveAllCustomer(Collection<Customer> customers);

    void deleteAllCustomer(Collection<Customer> customers);

    long countCustomer();
}
