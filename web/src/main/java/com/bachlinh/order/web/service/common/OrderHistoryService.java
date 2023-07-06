package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.resp.OrderHistoryResp;

import java.util.Collection;

public interface OrderHistoryService {
    Collection<OrderHistoryResp> getOrderHistoriesOfCustomer(Customer owner);
}
