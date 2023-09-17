package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerMedia;

public interface CustomerMediaRepository {
    CustomerMedia getCustomerMedia(Customer owner);

    void deleteCustomerMedia(CustomerMedia customerMedia);
}
