package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.resp.OrderListResp;

import java.util.Collection;

public interface OrderInDateService {
    Collection<OrderListResp> getOrdersInDate();

    int numberOrderInDate();
}
