package com.bachlinh.order.security.helper;

import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class RequestAccessHistoriesHolder {
    private static final int MAX_REQUEST_BATCH_SIZE = 50;
    private static final List<CustomerAccessHistory> HISTORIES = Collections.synchronizedList(new LinkedList<>());

    private RequestAccessHistoriesHolder() {
    }

    public static void saveHistories(CustomerAccessHistory customerAccessHistory, CustomerAccessHistoryRepository customerAccessHistoryRepository) {
        HISTORIES.add(customerAccessHistory);
        if (HISTORIES.size() == MAX_REQUEST_BATCH_SIZE) {
            var filtered = HISTORIES.stream().filter(history -> history.getCustomer() != null).toList();
            customerAccessHistoryRepository.saveAllCustomerAccessHistory(filtered);
            HISTORIES.clear();
        }
    }

    public static void flushAllHistories(CustomerAccessHistoryRepository customerAccessHistoryRepository) {
        if (!HISTORIES.isEmpty()) {
            var filtered = HISTORIES.stream().filter(history -> history.getCustomer() != null).toList();
            customerAccessHistoryRepository.saveAllCustomerAccessHistory(filtered);
            HISTORIES.clear();
        }
    }
}
