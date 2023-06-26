package com.bachlinh.order.web.service.business;

import org.springframework.data.domain.Page;
import com.bachlinh.order.web.dto.resp.OrderListResp;

public interface OrderInDateService {
    Page<OrderListResp> getOrdersInDate();

    int numberOrderInDate();
}
