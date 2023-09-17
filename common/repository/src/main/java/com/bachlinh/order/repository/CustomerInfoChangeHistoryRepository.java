package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.util.Collection;

public interface CustomerInfoChangeHistoryRepository extends NativeQueryRepository {

    void saveHistories(Collection<CustomerInfoChangeHistory> histories);

    void deleteHistories(Collection<CustomerInfoChangeHistory> histories);

    Collection<CustomerInfoChangeHistory> getHistoriesInYear();

    Collection<CustomerInfoChangeHistory> getHistoriesChangeOfCustomer(Customer customer, long limit);

    Collection<CustomerInfoChangeHistory> getHistoriesChangeOfCustomer(String customerId, long page, long pageSize);

    Long countChangeHistories(String customerId);
}
