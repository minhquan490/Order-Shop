package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.LoginHistory;

import java.util.Collection;

public interface LoginHistoryRepository {
    LoginHistory saveHistory(LoginHistory loginHistory);

    Collection<LoginHistory> getHistories(Customer owner);
}
