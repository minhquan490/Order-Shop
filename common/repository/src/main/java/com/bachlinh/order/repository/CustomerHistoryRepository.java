package com.bachlinh.order.repository;

import com.bachlinh.order.core.entity.model.Customer;
import com.bachlinh.order.core.entity.model.CustomerHistory;

import java.sql.Date;
import java.util.Collection;

public interface CustomerHistoryRepository {

    CustomerHistory saveCustomerHistory(CustomerHistory customerHistory);

    boolean deleteCustomerHistory(CustomerHistory customerHistory);

    boolean deleteAll(Collection<CustomerHistory> histories);

    Collection<CustomerHistory> getHistories(Customer customer);

    Collection<CustomerHistory> getHistoriesExpireNow(Date now);
}
