package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;

import java.sql.Date;
import java.util.Collection;

public interface CustomerAccessHistoryRepository {

    CustomerAccessHistory saveCustomerHistory(CustomerAccessHistory customerAccessHistory);

    boolean deleteCustomerHistory(CustomerAccessHistory customerAccessHistory);

    boolean deleteAll(Collection<CustomerAccessHistory> histories);

    Collection<CustomerAccessHistory> getHistories(Customer customer);

    Collection<CustomerAccessHistory> getHistoriesExpireNow(Date now);
}
