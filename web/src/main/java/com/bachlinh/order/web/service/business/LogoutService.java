package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.Customer;

public interface LogoutService {

    boolean logout(Customer customer, String secret);
}
