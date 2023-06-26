package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;

import java.util.Collection;

public interface CustomerInfoChangerHistoryRepository {
    void saveHistory(CustomerInfoChangeHistory history);

    void saveHistories(Collection<CustomerInfoChangeHistory> histories);
}
