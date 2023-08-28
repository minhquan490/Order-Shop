package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.CustomerAccessHistory;

import java.sql.Date;
import java.util.Collection;

public interface CustomerAccessHistoryRepository extends NativeQueryRepository {

    void saveCustomerHistory(CustomerAccessHistory customerAccessHistory);

    void saveAllCustomerAccessHistory(Collection<CustomerAccessHistory> customerAccessHistories);

    boolean deleteCustomerHistory(CustomerAccessHistory customerAccessHistory);

    void deleteAll(Collection<CustomerAccessHistory> histories);

    Collection<CustomerAccessHistory> getHistoriesExpireNow(Date now);
}
