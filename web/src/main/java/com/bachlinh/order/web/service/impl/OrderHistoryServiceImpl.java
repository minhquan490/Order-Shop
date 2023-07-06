package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.OrderHistoryRepository;
import com.bachlinh.order.web.dto.resp.OrderHistoryResp;
import com.bachlinh.order.web.service.common.OrderHistoryService;

import java.util.Collection;

@ServiceComponent
@RequiredArgsConstructor(onConstructor = @__(@ActiveReflection))
@ActiveReflection
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;
    private final DtoMapper dtoMapper;

    @Override
    public Collection<OrderHistoryResp> getOrderHistoriesOfCustomer(Customer owner) {
        var histories = orderHistoryRepository.getHistoriesOfCustomer(owner);
        return dtoMapper.map(histories, OrderHistoryResp.class);
    }
}
