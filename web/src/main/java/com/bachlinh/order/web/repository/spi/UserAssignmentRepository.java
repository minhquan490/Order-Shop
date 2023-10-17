package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Voucher;

/**
 * Repository for USER_ASSIGNMENT table
 */
public interface UserAssignmentRepository {
    void deleteUserAssignmentOfCustomer(Customer customer);

    void deleteUserAssignment(Voucher voucher);
}
