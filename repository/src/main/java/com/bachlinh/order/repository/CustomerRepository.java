package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.repository.NativeQueryRepository;
import com.bachlinh.order.entity.repository.query.OrderBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

public interface CustomerRepository extends NativeQueryRepository {

    Customer getCustomerBasicInformation(String customerId);

    Customer getCustomerForRefreshTokenGeneration(String customerId);

    Customer getCustomerForAuthentication(String customerId);

    Customer getCustomerForLogin(String username);

    Customer getCustomerForResetPassword(String email);

    Customer getCustomerForEmailSending(String customerId);

    Customer getCustomerUpdatableInfo(String customerId);

    Customer getCustomerInfoForUpdate(String customerId);

    Customer getCustomerForDelete(String customerId);

    Customer getFullInformation(String customerId);

    Customer saveCustomer(@NonNull Customer customer);

    Customer updateCustomer(@NonNull Customer customer);

    void updateCustomers(Collection<Customer> customers);

    void deleteCustomer(@NonNull Customer customer);

    boolean isEmailExisted(String email);

    boolean isPhoneNumberExisted(String phoneNumber);

    boolean isUsernameExisted(String username);

    boolean isCustomerIdExisted(String customerId);

    Collection<Customer> getCustomerByIds(Collection<String> ids);

    Page<Customer> getAll(@Nullable Pageable pageable, Collection<OrderBy> orderByCollection);
}
