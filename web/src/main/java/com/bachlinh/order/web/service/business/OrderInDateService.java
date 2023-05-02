package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.resp.OrderInDateResp;
import org.springframework.data.domain.Page;

public interface OrderInDateService {
    Page<OrderInDateResp> getOrdersInDate();

    int numberOrderInDate();
}
