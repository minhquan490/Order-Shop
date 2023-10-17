package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.sql.Date;
import java.util.Collection;

public interface CustomerAccessHistoryRepository extends NativeQueryRepository {

    void saveCustomerHistory(CustomerAccessHistory customerAccessHistory);

    void saveAllCustomerAccessHistory(Collection<CustomerAccessHistory> customerAccessHistories);

    void deleteAll(Collection<CustomerAccessHistory> histories);

    Collection<CustomerAccessHistory> getHistoriesExpireNow(Date now);

    Collection<CustomerAccessHistory> getHistoriesOfCustomer(Customer customer);

    Collection<CustomerAccessHistory> getHistoriesOfCustomer(Customer customer, long page, long pageSize);

    Long countAccessHistoriesOfCustomer(String customerId);
}
