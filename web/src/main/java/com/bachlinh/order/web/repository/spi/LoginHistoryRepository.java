package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface LoginHistoryRepository extends NativeQueryRepository {
    void saveHistory(LoginHistory loginHistory);

    Collection<LoginHistory> getHistories(Customer owner, long limit);

    Collection<LoginHistory> getHistoriesOfCustomer(String customerId, long page, long pageSize);

    Collection<LoginHistory> getHistories(Customer owner);

    Long countHistoriesOfCustomer(String customerId);

    void deleteLoginHistories(Collection<LoginHistory> loginHistories);
}
