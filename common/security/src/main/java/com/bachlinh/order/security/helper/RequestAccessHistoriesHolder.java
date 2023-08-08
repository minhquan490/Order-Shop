package com.bachlinh.order.security.helper;

import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestAccessHistoriesHolder {
    private static final int MAX_REQUEST_BATCH_SIZE = 50;
    private static final List<CustomerAccessHistory> HISTORIES = Collections.synchronizedList(new LinkedList<>());

    public static void saveHistories(CustomerAccessHistory customerAccessHistory, CustomerAccessHistoryRepository customerAccessHistoryRepository) {
        HISTORIES.add(customerAccessHistory);
        if (HISTORIES.size() == MAX_REQUEST_BATCH_SIZE) {
            customerAccessHistoryRepository.saveAllCustomerAccessHistory(HISTORIES);
            HISTORIES.clear();
        }
    }

    public static void flushAllHistories(CustomerAccessHistoryRepository customerAccessHistoryRepository) {
        if (!HISTORIES.isEmpty()) {
            customerAccessHistoryRepository.saveAllCustomerAccessHistory(HISTORIES);
            HISTORIES.clear();
        }
    }
}
