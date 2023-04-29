package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.LoginHistory;

public interface LoginHistoryRepository {
    LoginHistory saveHistory(LoginHistory loginHistory);
}
