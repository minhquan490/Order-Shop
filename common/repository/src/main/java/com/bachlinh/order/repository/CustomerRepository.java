package com.bachlinh.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Where;

import java.util.Collection;

public interface CustomerRepository extends NativeQueryRepository {

    Customer getCustomer(@NonNull Collection<Where> wheres, @NonNull Collection<Join> joins);

    Customer getCustomerById(String id, boolean useJoin);

    Customer getCustomerByUsername(String username);

    Customer getCustomerByEmail(String email);

    Customer getCustomerUseJoin(Object customerId, Collection<Join> joins);

    Customer getCustomerByPhone(String phone);

    Customer saveCustomer(@NonNull Customer customer);

    Customer updateCustomer(@NonNull Customer customer);

    boolean deleteCustomer(@NonNull Customer customer);

    boolean usernameExist(String username);

    boolean phoneNumberExist(String phone);

    boolean emailExist(String email);

    boolean existById(Object customerId);

    Collection<Customer> getCustomers(@NonNull Collection<Where> wheres, @NonNull Collection<Join> joins, @Nullable Pageable pageable, @Nullable Sort sort);

    Collection<Customer> getCustomerByIds(Collection<String> ids);

    Page<Customer> getAll(@Nullable Pageable pageable, @Nullable Sort sort);

    long countCustomer();
}
